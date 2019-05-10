import items
import Backpack
import genetic_algorithm_operator as gao
import json
from matplotlib import pyplot as plt

pop_size = 100
prob_crossover = 0.7
prob_mutation = 0.05
num_generations = 500
penalize = True

plot_graph = True
best_fitnesses_per_gen = []
best_exec_fitness = 0

def secondField(entry):
    return entry[1]

def bpFitness(entry):
    return entry._fitness

def execute(item_set: dict):

    global pop_size
    global prob_crossover
    global prob_mutation
    global num_generations
    global penalize

    global plot_graph
    global best_fitnesses_per_gen
    global best_exec_fitness

    population = []
    children = []
    for i in range(pop_size):
        bp = Backpack.Backpack(items=item_set, max_weight=113, penalize=penalize)
        bp.randomly_fill()
        population.append(bp)
    for i in range(pop_size):
        bp = Backpack.Backpack(items=item_set, max_weight=113, penalize=penalize)
        children.append(bp)

    fitnesses_per_gen = []
    for gen_count in range(num_generations):
        fitnesses = []
        for bp in population:
            bp.update_fitness()
            fitnesses.append(bp._fitness)
        selected_backpacks = gao.roulette_select(fitnesses, len(population))

        for parent_index in range(0, len(selected_backpacks), 2):
            child_gene1, child_gene2 = gao.crossover(population[selected_backpacks[parent_index]].chromossome(), population[selected_backpacks[parent_index+1]].chromossome(), prob_crossover)
            child_gene1 = gao.mutate(child_gene1, prob_mutation)
            child_gene2 = gao.mutate(child_gene1, prob_mutation)
            children[parent_index].load_from_gene(child_gene1)
            children[parent_index+1].load_from_gene(child_gene2)

        pop_fitnesses = []
        child_fitnesses = []
        for i,bp in enumerate(population):
            bp.update_fitness()
            pop_fitnesses.append((i, bp._fitness))
        for i,bp in enumerate(children):
            bp.update_fitness()
            child_fitnesses.append((i, bp._fitness))
        pop_fitnesses.sort(key=secondField, reverse=True)
        child_fitnesses.sort(key=secondField, reverse=True)
        pop_index = 0
        child_index = 0
        new_pop = []
        for i in range(pop_size):
            if child_fitnesses[child_index][1] >= pop_fitnesses[pop_index][1]:
                new_pop.append(children[child_fitnesses[child_index][0]])
                child_index += 1
            else:
                new_pop.append(population[pop_fitnesses[pop_index][0]])
                pop_index += 1
        population = new_pop
        if plot_graph:
            fitnesses_per_gen.append(population[0]._fitness)

    population.sort(key=bpFitness, reverse=True)

    if plot_graph and population[0]._fitness > best_exec_fitness:
        best_fitnesses_per_gen = fitnesses_per_gen
        best_exec_fitness = population[0]._fitness

    return population[0], population[0]._fitness


num_executions = 10
with open('items.json', 'r') as f:
    item_set = json.load(f)

    #try:
    r = open('results.json', 'r+')
    results = json.load(r)
    #except:
        #r = open('results.json', 'w')
        #results = {}

    id = 0
    while str(id) in results.keys():
        id += 1

    new_result = {}
    new_result['pop_size'] = pop_size
    new_result['prob_crossover'] = prob_crossover
    new_result['prob_mutation'] = prob_mutation
    new_result['num_generations'] = num_generations
    new_result['penalize'] = penalize

    acc = 0
    for execution_count in range(num_executions):
        best_backpack, best_fitness = execute(item_set)
        acc += best_fitness
        new_result[('exec'+str(execution_count))] = {}
        new_result[('exec'+str(execution_count))]['best_backpack'] = str(best_backpack)
        new_result[('exec'+str(execution_count))]['best_fitness'] = str(best_fitness)
    new_result['fitness_mean'] = acc/num_executions

    results[str(id)] = new_result
    r.truncate(0)
    json.dump(results, r)
    if plot_graph:
        plt.plot(best_fitnesses_per_gen, marker='d', color='blue')#, drawstyle='steps-pre')
        plt.show()
