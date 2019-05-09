import random

def roulette_select(fitness: list, chromosomes_num: int):
    return random.choices(population=range(len(fitness)), weights=fitness, k=chromosomes_num)

def crossover(gene1: list, gene2: list):
    cross_pop = range(1,len(gene1)//2)
    cross_weights = list(map(lambda x: x*x, reversed(cross_pop)))
    n_cross = random.choices(population=cross_pop, weights=cross_weights)[0]

    division = random.choices(population=range(1,len(gene1)), cum_weights=range(1,len(gene1)), k=n_cross)
    division.sort()
    division.append(len(gene1))

    last_cut = 0
    new_gene1 = []
    new_gene2 = []
    for i,cut in enumerate(division):
        if cut != last_cut:
            if not i%2:
                new_gene1 += gene2[last_cut:cut]
                new_gene2 += gene1[last_cut:cut]
            else:
                new_gene1 += gene1[last_cut:cut]
                new_gene2 += gene2[last_cut:cut]
            last_cut = cut
    return new_gene1, new_gene2

def mutate(gene: list, mutate_probability):
    for allele in gene:
        if random.uniform(0,1) < mutate_probability:
            allele = not allele
    return gene
