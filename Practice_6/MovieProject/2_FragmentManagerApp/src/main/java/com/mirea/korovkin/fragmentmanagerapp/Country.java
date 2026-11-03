package com.mirea.korovkin.fragmentmanagerapp;

// простая модель страны, только поля
public class Country {
    private final String flag;
    private final String name;
    private final String capital;
    private final String population;
    private final String description;

    public Country(String flag, String name, String capital, String population, String description) {
        this.flag = flag;
        this.name = name;
        this.capital = capital;
        this.population = population;
        this.description = description;
    }

    public String getFlag() { return flag; }
    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getPopulation() { return population; }
    public String getDescription() { return description; }

    // ArrayAdapter рисует строку через toString — флаг + название
    @Override
    public String toString() {
        return flag + "  " + name;
    }
}
