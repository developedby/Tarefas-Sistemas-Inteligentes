
class Items:
    def __init__(self):
        self._item_dict = {}
        self._default_name = 0

    def add_item(self, weight: int, value: int, name: str = None):
        if not name:
            while str(self._default_name) in self._item_dict.keys():
                self._default_name += 1
            name = str(self._default_name)
        self._item_dict[name] = {}
        self._item_dict[name]['weight'] = weight
        self._item_dict[name]['value'] = value

    def remove_item(self, name: str):
        if name in self._item_dict.keys():
            self._item_dict.pop(name)

    def __iter__(self):
        return iter(self._item_dict)

    def __next__(self):
        return next(self._item_dict)

items = Items()
items.add_item(2,9,'hey')
items.add_item(3,8,'you')

for item in items:
    print(item)
