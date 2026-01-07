import java.util.ArrayList;
import java.util.List;


public class Dish {
    private int id;
    private String name;
    private dishType DishTypeEnum;
    private List<Ingredient> ingredient;

    public enum dishType {
        STARTER,
        MAIN_COURSE,
        DESSERT
    }

    public Dish(int id, String name, dishType DishTypeEnum, List<Ingredient> ingredient, Double getDishPrice) {
        this.id = id;
        this.name = name;
        this.DishTypeEnum = DishTypeEnum;
        this.ingredient = ingredient;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public dishType getDishTypeEnum() {
        return DishTypeEnum;
    }
    public List<Ingredient> getIngredient() {
        return ingredient;
    }

    // Méthode pour calculer le prix total du plat en fonction des ingrédients
        public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Ingredient ingredient : ingredients) {
            if (ingredient != null) {
                total += ingredient.getCostWithQuantity();
            }
        }
        return total;
    }

    // Méthode pour ajouter un ingrédient
    public void addIngredient(Ingredient ing) {
        if (ingredient == null) {
            ingredient = new ArrayList<>();
        }
        ingredient.add(ing);
    }

    @Override
    public String toString() {
        return String.format("Dish{id=%d, name='%s', type=%s, cost=%.2f, ingredients=%d}",
                id, name, DishTypeEnum, getDishCost(), ingredient != null ? ingredient.size() : 0);
    }
}

