"""
Run a rest API exposing the yolov5s object detection model
"""
from ultralytics import YOLO

import argparse
import io
import os
import time

from flask import Flask, request, send_file
from flask_cors import CORS

from flask_ngrok import run_with_ngrok

from PIL import Image
import shutil

app = Flask(__name__)
CORS(app)
#run_with_ngrok(app)

#app.config['MAX_CONTENT_LENGTH'] = 100 * 1024 * 1024  # 100MB limi

DETECTION_URL = "/yolo/object-detection"
    
@app.route(DETECTION_URL, methods=["POST"])
def predictTest():
    if not request.method == "POST":
        return
    
    request_start = round(time.time() * 1000) #Arrived request ms 
    
    if request.form:
        request_dict = request.form.to_dict()
        start_timestamp = int(request_dict["timestamp"])
        diff = request_start - start_timestamp
        print("travel time: " + str(diff))

    if request.files.get("image"):

        #Get image file from post body
        image_file = request.files["image"]
        
        #Create unique timestamp for origin and detected file
        unique_id = str(round(time.time() * 1000))

        #Create unique folder for origin and detected files
        directory_res = os.path.join(".", "runs", "detect", "request_" + str(unique_id))
        directory_res_increment = os.path.join(directory_res, "detect")
        os.makedirs(directory_res_increment)

        #Origin file
        originFilePath = os.path.join(directory_res_increment, "origin.jpg")
        image_file.save(originFilePath)

        #reopen file 
        image_bytes = open(originFilePath, 'rb').read()

        img = Image.open(io.BytesIO(image_bytes))
        
        #Image original size
        print("image size:")
        print(img.size)
        
        #Prediction  
        start_prediction = time.time()  
        try:  
            model.predict(source=originFilePath, project=directory_res_increment, name="result", save=True, imgsz=320) #, imgsz=320)  # reduce size=320 for faster inference
        except:
            shutil.rmtree(directory_res)
            #return
        end_prediction = time.time()
        print("Elapsed time prediction [ms]:")
        print((end_prediction - start_prediction)*1000)

        #print("Detection result:")
        #print(results)

        #detected file like byte array
        try:
            detected_byte_array = open(os.path.join(directory_res_increment, "result", "origin.jpg"), 'rb').read() #"detect" folder is incremented by save function
        except:
            shutil.rmtree(directory_res)
            #return
        print("Result bytes: " + str(len(detected_byte_array)))
        #detected_byte_array = Image.open(os.path.join(directory_res_increment, "result", "origin.jpg"))
        
        #Rescale image
        #newsize = (256, 320)
        #detected_byte_array = detected_byte_array.resize(newsize)

        #remove file and folder
        shutil.rmtree(directory_res)

        end = round(time.time() * 1000)

        elapsedTime = (end - request_start)
        print("Elapsed time [ms]:")
        print(elapsedTime)

        #buf = io.BytesIO()
        #detected_byte_array.save(buf, "jpeg", quality=70)
        #buf.seek(0) #Set file postion to 0

        return send_file(
            io.BytesIO(detected_byte_array), #This create a byte string suitable to send.
            mimetype='image/jpg'
        )
    
@app.route("/test", methods=['GET', 'POST'])
def getTest():
    # Images
    img = "./zidane.jpg" #"https://ultralytics.com/images/zidane.jpg"  # or file, Path, PIL, OpenCV, numpy, list

    # Inference
    start_prediction = time.time()  
    model.predict(img)
    end_prediction = time.time()
    print("Elapsed time prediction [ms]:")
    print((end_prediction - start_prediction) * 1000)

    detected_byte_array = open(img, 'rb').read()

    # Results
    return send_file(
            io.BytesIO(detected_byte_array),
            mimetype='image/jpg'
        )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Flask API exposing YOLOv5 model")
    parser.add_argument("--port", default=5000, type=int, help="port number")
    args = parser.parse_args()

    model = YOLO('yolov8n.pt')  # force_reload to recache
    app.run(host="0.0.0.0", port=args.port)  #  ssl_context="adhoc" debug=True causes Restarting with stat
    #app.run(region="ue")