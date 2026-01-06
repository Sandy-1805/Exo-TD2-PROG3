public class Ingredient {
    private int id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Dish dish;
    public enum CategoryEnum {
        VEGETABLE,
        ANIMAL,
        MARINE,
        DAIRY,
        OTHER
    }
    public Ingredient(int id, String name, Double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
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

    @Override
    public String toString() {
        return String.format("Ingredient{id=%d, name='%s', price=%.2f, category=%s}",
                id, name, price, category);
    }
}
