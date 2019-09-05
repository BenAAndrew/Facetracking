import configparser

config = configparser.ConfigParser()
config.read('config.ini')

booleans = dict(config['Booleans'])
for key, value in booleans.items():
    booleans[key] = bool(value == "True")
    
integers = dict(config['Integers'])
for key, value in integers.items():
    integers[key] = int(value)
    
decimals = dict(config['Decimals'])
for key, value in decimals.items():
    decimals[key] = float(value)
    
properties = {**booleans, **integers, **decimals}