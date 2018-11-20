import time
import os


def current_time_millis():
    return int(round(time.time() * 1000))


def text_files(path):
    file_names = os.listdir(path)
    return [file_name for file_name in file_names if file_name[-4:] == '.txt']
