items_default_name = 0

def add_item(item_dict: dict, weight: int, value: int, name: str = None):
    if not name:
        global items_default_name
        while str(items_default_name) in item_dict.keys():
            items_default_name += 1
        name = str(items_default_name)
    item_dict[name] = {}
    item_dict[name]['weight'] = weight
    item_dict[name]['value'] = value

def remove_item(item_dict: dict, name: str):
    if name in item_dict.keys():
        item_dict.pop(name)
