import random

def secondField(entry):
    return entry[1]

class Backpack:
    def __init__(self, items: dict, max_weight: int, penalize: bool = False):
        self._available_items = items
        self._stored_items = {}
        self._max_weight = max_weight
        self._weight = 0
        self._value = 0
        self._penalize = penalize
        self._fitness = 0

    def add_item(self, name: str):
        item = self._available_items.pop(name)
        self._weight += item['weight']
        self._value += item['value']
        self._stored_items[name] = item

    def remove_item(self, name: str):
        item = self._stored_items.pop(name)
        self._weight -= item['weight']
        self._value -= item['value']
        self._available_items[name] = item

    def update_fitness(self):
        self._fitness = value
        if self._weight > self._max_weight:
            if self.penalize == True:
                self.penalize_fitness()
            else:
                self.repair_backpack()

    def penalize_fitness(self):
        self._fitness = 0

    def repair_backpack(self):
        while self._weight > self.max_weight:
            value_list = []
            for key in self._stored_items.keys():
                value_list.append((key, self._stored_items[key]['value']))
            value_list.sort(key = secondField)

            self.remove_item(value_list[0][0])
            self._fitness = self._value

    def randomly_fill(self):
        while True:
            random_index = random.randint(0, len(self._available_items.keys())-1)
            random_name = self._available_items.keys()[random_index]
            if self._weight + self._available_items[random_name] > self._max_weight:
                break
            else:
                self.add_item(random_name)

    def __str__(self):
        s = '\nBackpack:\n'
        for key in self._stored_items.keys():
            s += key + ': Weight = ' + self._stored_items[key]['weight']
            s += ', Value = ' + self._stored_items[key]['value'] + '\n'
        s += 'Total: Weight = ' + self._weight + ', Value = ' + self._value
        return s
