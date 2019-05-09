import items
import Backpack
import genetic_algorithm_operator as gao
import json

N_POPULATION = 100

with open('items.json', 'r') as f:
    item_set = json.load(f)

    population = []
    children = []

    for i in range(N_POPULATION):
        population.append(Backpack.Backpack(items=item_set, max_weight=113, penalize=False))

    for i in range(N_POPULATION):
        children.append(Backpack.Backpack(items=item_set, max_weight=113, penalize=False))
