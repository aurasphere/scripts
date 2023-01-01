import hashlib
import base64
import os
stringToHash = os.environ['ESPANSO_STRING']
result = hashlib.md5(stringToHash.encode())
print(base64.b64encode(result.digest()).decode())