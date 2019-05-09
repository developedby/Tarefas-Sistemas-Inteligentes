import items
import Backpack
import genetic_algorithm_operator as gao
import json

def execute():
    pop_size = 100
    prob_crossover = 0.7
    prob_mutation = 0.01
    num_generations = 500

    population = []
    children = []
    for i in range(pop_size):
        population.append(Backpack.Backpack(items=item_set, max_weight=113, penalize=False))
    for i in range(pop_size):
        children.append(Backpack.Backpack(items=item_set, max_weight=113, penalize=False))

    for gen_count in num_generations:
        fitnesses = []
        for bp in population:
            bp.update_fitness()
            fitnesses.append(pb._fitness)
        selected_backpacks = roulette_select(fitnesses, len(population))

        for parent_index in range(0, len(selected_backpacks), 2):
            child_gene1, child_gene2 = crossover(selected_backpacks[parent_index], selected_backpacks[parent_index+1], prob_crossover)
            child_gene1 = mutate(child_gene1, prob_mutation)
            child_gene2 = mutate(child_gene1, prob_mutation)
            children[parent_index].load_from_gene(child_gene1)
            children[parent_index+1].load_from_gene(child_gene2)


num_executions = 10
with open('items.json', 'r') as f:
    item_set = json.load(f)
    for execution_count in range(num_executions):
        execute()
