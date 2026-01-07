public class Ingredient {
    private int id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Dish dish;
    private Double requiredQuantity;

    public enum CategoryEnum {
        VEGETABLE,
        ANIMAL,
        MARINE,
        DAIRY,
        OTHER
    }

    public Ingredient(int id, String name, Double price, CategoryEnum category, Dish dish) {
        this(id, name, price, null, category, dish);
    }

    public Ingredient(int id, String name, Double price, Double requiredQuantity, 
                     CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.requiredQuantity = requiredQuantity;
        this.category = category;
        this.dish = dish;
    }

    public Double getRequiredQuantity() {
        return requiredQuantity;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Double getPrice() {
        return price;
    }
    public CategoryEnum getCategory() {
        return category;
    }
    public Dish getDish() {
        return dish;
    }

    public void setRequiredQuantity(Double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

     // Méthode pour calculer le coût avec la quantité
    public Double getCostWithQuantity() {
        if (price == null) return 0.0;
        if (requiredQuantity == null) return price;
        return price * requiredQuantity;
    }

    @Override
    public String toString() {
        return String.format("Ingredient{id=%d, name='%s', price=%.2f, quantity=%s, cost=%.2f, category=%s}",
                id, name, price, 
                requiredQuantity != null ? requiredQuantity.toString() : "N/A",
                getCostWithQuantity(), category);
    }
}
