FROM python:3-slim
ADD worker.py /
RUN pip install pika redis geopy
ENTRYPOINT ["python","./worker.py"]