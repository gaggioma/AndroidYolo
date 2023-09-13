#Test yoloV8
from ultralytics import YOLO
import time

# Load a model
model = YOLO('yolov8n.pt')  # load an official model

# Predict with the model
start_prediction = time.time()    
model.predict('./zidane.jpg')  # predict on an image
end_prediction = time.time()
print("prediction time: ", (end_prediction-start_prediction)*1000, " ms")

start_prediction = time.time()    
model.predict('./bus.jpg')  # predict on an image
end_prediction = time.time()
print("prediction time: ", (end_prediction-start_prediction)*1000, " ms")
#print(results)