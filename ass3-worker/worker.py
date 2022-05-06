# TODO Now the project compiler is python. Later you need to set again Java
#  Modify the Workload monitor test or create a new one to test this worker
import sys
import pika
import json
import time
import redis
import random
import signal
from geopy import distance

if len(sys.argv) < 1:
    print("Region argument is empty. Please provide a valid region when calling this script.")
    exit(0)
region = sys.argv[1]
if region != ('at_vienna' or 'at_linz' or 'de_berlin'):
    print("Region argument is invalid. Valid regions are: at_vienna, at_linz and de_berlin")
    exit(0)
print("Started a new Python Worker for region " + region + ".")


# called when docker is closed
def signal_handler(sig, frame):
    print('Worker terminated his work.')
    sys.exit(0)


signal.signal(signal.SIGTERM, signal_handler)

credentials = pika.PlainCredentials(username='dst', password='dst')
parameters = pika.ConnectionParameters(host='127.0.0.1', port=5672, virtual_host='/', credentials=credentials)
connection = pika.BlockingConnection(parameters=parameters)
channel = connection.channel()
print("Worker connected to RabbitMQ")
queue_name = 'dst.' + region
r = redis.Redis()
r.ping()
print("Worker connected to Redis")
secs = 0
if region == 'at_linz':
    secs = random.randint(1, 2)
if region == 'at_vienna':
    secs = random.randint(3, 5)
if region == 'de_berlin':
    secs = random.randint(8, 11)


def callback(ch, method, properties, body):
    # trip_request is a Python dict
    trip_request = json.loads(body.decode('utf-8'))
    pickup_coords = (trip_request['pickup']['longitude'], trip_request['pickup']['latitude'])
    print("Worker consumed a trip request from the queue " + queue_name)
    print("Trip request: " + str(trip_request))
    start_time = 0
    min_distance_driver = ''

    deleted_keys_num = 0
    while deleted_keys_num == 0:
        start_time = time.time()
        print("Request processing start time saved: " + str(start_time))
        print("Query available drivers from Redis")
        rediskey = 'drivers:' + region
        drivers = r.hgetall(rediskey)
        if not drivers:
            print("All drivers are currently busy")
            worker_response = {
                'requestId': trip_request['id'],
                'processingTime': 0,
                'driverId': ''
            }
            send_response(worker_response)
            return
        else:
            print("Calculating the distances between all the available drivers and the rider")
            distances = {}
            for key in drivers:
                coords = drivers[key]
                driver_coords = coords.split()
                dist = distance.distance(pickup_coords, driver_coords).km
                distances[key] = dist
            min_distance_driver = min(distances, key=distances.get).decode('utf8')
            print("The nearest driver has ID = " + str(min_distance_driver))
            deleted_keys_num = r.hdel(rediskey, min_distance_driver)
        if deleted_keys_num == 0:
            print("The matched driver is no longer available. Restarting match process.")

    print("Driver removed from redis")
    time.sleep(secs)
    processing_time = time.time() - start_time
    print("The total processing time was: " + str(processing_time) + "s")
    worker_response = {
        'requestId': trip_request['id'],
        'processingTime': processing_time,
        'driverId': min_distance_driver
    }
    send_response(worker_response)


def send_response(worker_response):
    json_response = json.dumps(worker_response)
    print("Created Worker response. Ready to sent.")
    print("Worker response: " + json_response)
    routing_key = 'requests.' + region
    channel.basic_publish(exchange='dst.workers', routing_key=routing_key, body=json_response.encode('utf8'))
    print("Worker response succesfully sent on the dst.workers RabbitMQ exchange")


channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)
print("Worker listening on queue " + queue_name)
channel.start_consuming()
