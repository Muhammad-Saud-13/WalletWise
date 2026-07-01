import urllib.request
import urllib.error

url = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyFakeKey'
req = urllib.request.Request(url, method='POST', data=b'{"contents":[{"parts":[{"text":"Hello"}]}]}', headers={'Content-Type': 'application/json'})

try:
    with urllib.request.urlopen(req) as response:
        print("Success:", response.read())
except urllib.error.HTTPError as e:
    print("HTTPError:", e.code, e.reason)
    print("Body:", e.read().decode())

