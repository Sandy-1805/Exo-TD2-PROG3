import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    public Dish findDishById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas etre null");
        }

        Dish dish = null;
        Connection conn = null;

        try {
            conn = dbConnection.getConnection();
            //Récuperer les informations du plat
            String dishSql = "SELECT id, name, type FROM dish WHERE id = ?";
            try(PreparedStatement dishStmt = conn.prepareStatement(dishSql)) {
                dishStmt.setInt(1, id);

                try(ResultSet rs = dishStmt.executeQuery()) {
                    if (rs.next()) {
                        dish = new Dish(
                                rs.getInt("id"),
                                rs.getString("name"),
                                Dish.dishType.valueOf(rs.getString("type")),
                                null,
                                0.0
                        );
                    } else {
                        return null;
                    }
                }
            }
            //Récuperer tous les ingredients du plat
            if (dish != null) {
                String ingredtientSql = "SELECT id, name, price, required_quantity, category " +
                                        "FROM ingredient WHERE dish_id = ?";
                try(PreparedStatement ingStmt = conn.prepareStatement(ingredtientSql)) {
                    ingStmt.setInt(1, id);

                    try(ResultSet ingRs = ingStmt.executeQuery()) {
                        List<Ingredient> ingredients = new ArrayList<>();

                        while (ingRs.next()) {
                            Ingredient ingredient = new Ingredient(
                                    ingRs.getInt("id"),
                                    ingRs.getString("name"),
                                    ingRs.getDouble("price"),
                                    ingRs.getObject("required_quantity") != null ? rs.getDouble("required_quantity") : null,
                                    Ingredient.CategoryEnum.valueOf(ingRs.getString("category")),
                                    dish
                            );
                            ingredients.add(ingredient);
                        }
                    }
                }
            }
        } finally {
            if (conn != null) {
                dbConnection.closeConnection(conn);
            }
        }
        return dish;

    }

    public static void main() {
        DataRetriever retrevier = new DataRetriever();

        try {
            Dish dish = retrevier.findDishById(1);

            if(dish != null) {
                System.out.println("Plat trouvé : " + dish.getName());
                System.out.println("Type : " + dish.getDishTypeEnum());
                System.out.println("Ingredients : " + dish.getIngredient().size());
                System.out.println("Prix total : " + dish.getDishPrice());

                System.out.println("\nIngredients : ");
                for (Ingredient ing : dish.getIngredient()) {
                    System.out.println(" - " + ing.getName() + " ( " 
                                             + ing.getPrice() + "Ar, " + 
                                            ing.getCategory() + " )");
                }
            } else {
                System.out.println("Aucun plat trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des plats : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Ingredient> findIngredients(int page, int size) throws SQLException {
        if (page < 1) {
            throw new IllegalArgumentException("Le numéro de page doit être >= 1");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("La taille de page doit être > 0");
        }
        
        // Calcul de l'offset (en supposant que page commence à 1)
        int offset = (page - 1) * size;
        
        List<Ingredient> ingredients = new ArrayList<>();
        
        // Utilisation de try-with-resources pour fermeture automatique
        try (Connection conn = dbConnection.getConnection()) {
            // Requête avec LIMIT et OFFSET pour la pagination
            String sql = "SELECT i.id, i.name, i.price, i.category, " +
                        "d.id as dish_id, d.name as dish_name, d.type as dish_type " +
                        "FROM ingredient i " +
                        "LEFT JOIN dish d ON i.id_dish = d.id " +
                        "ORDER BY i.id " +
                        "LIMIT ? OFFSET ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, size);
                stmt.setInt(2, offset);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Récupérer les données de l'ingrédient
                        int ingredientId = rs.getInt("id");
                        String ingredientName = rs.getString("name");
                        Double price = rs.getDouble("price");
                        String categoryStr = rs.getString("category");
                        
                        // Créer l'objet Dish si l'ingrédient est associé à un plat
                        Dish dish = null;
                        if (rs.getObject("dish_id") != null) {
                            int dishId = rs.getInt("dish_id");
                            String dishName = rs.getString("dish_name");
                            String dishTypeStr = rs.getString("dish_type");
                            
                            dish = new Dish(
                                dishId,
                                dishName,
                                Dish.dishType.valueOf(dishTypeStr),
                                null,
                                0.0
                            );
                        }
                        
                        // Créer l'ingrédient
                        Ingredient ingredient = new Ingredient(
                            ingredientId,
                            ingredientName,
                            price,
                            Ingredient.CategoryEnum.valueOf(categoryStr),
                            dish
                        );
                        
                        ingredients.add(ingredient);
                    }
                }
            }
        }
        
        return ingredients;
    }

    public static void testPagination() {
        DataRetriever retriever = new DataRetriever();
        
        try {
            System.out.println("=== Test pagination (page 1, taille 2) ===");
            List<Ingredient> page1 = retriever.findIngredients(1, 2);
            System.out.println("Page 1 - " + page1.size() + " ingrédients:");
            for (Ingredient ing : page1) {
                System.out.println(" - " + ing.getName() + " (" + ing.getPrice() + " €)");
            }
            
            System.out.println("\n=== Test pagination (page 2, taille 2) ===");
            List<Ingredient> page2 = retriever.findIngredients(2, 2);
            System.out.println("Page 2 - " + page2.size() + " ingrédients:");
            for (Ingredient ing : page2) {
                System.out.println(" - " + ing.getName() + " (" + ing.getPrice() + " €)");
            };
                
        } catch (SQLException e) {
            System.err.println("Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Paramètre invalide: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        testPagination();
    }
    



    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
    if (newIngredients == null || newIngredients.isEmpty()) {
        return new ArrayList<>();
    }
    
    List<Ingredient> createdIngredients = new ArrayList<>();
    Connection conn = null;
    
    try {
        conn = dbConnection.getConnection();
        // Vérifier d'abord si des ingrédients existent déjà
        checkExistingIngredients(conn, newIngredients);
        
        // Insérer tous les nouveaux ingrédients
        String sql = "INSERT INTO ingredient (name, price, category, id_dish) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (Ingredient ingredient : newIngredients) {
                stmt.setString(1, ingredient.getName());
                stmt.setDouble(2, ingredient.getPrice());
                stmt.setString(3, ingredient.getCategory().name());
                
                if (ingredient.getDish() != null) {
                    stmt.setInt(4, ingredient.getDish().getId());
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }
                
                stmt.executeUpdate();
                
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        Ingredient createdIngredient = new Ingredient(
                            newId,
                            ingredient.getName(),
                            ingredient.getPrice(),
                            ingredient.getCategory(),
                            ingredient.getDish()
                        );
                        createdIngredients.add(createdIngredient);
                    }
                }
            }
        }
        
        // Tout s'est bien passé, valider la transaction
        conn.commit();
        
    } catch (SQLException e) {
        // En cas d'erreur, annuler la transaction
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Erreur lors du rollback", rollbackEx);
            }
        }
        throw new RuntimeException("Erreur lors de la création des ingrédients: " + e.getMessage(), e);
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignorer les erreurs de fermeture
            }
        }
    }
    
    return createdIngredients;
}

    private void checkExistingIngredients(Connection conn, List<Ingredient> ingredients) throws SQLException {
        // Vérifier si un ingrédient avec le même nom existe déjà
        String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ?";
        
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            for (Ingredient ingredient : ingredients) {
                checkStmt.setString(1, ingredient.getName());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new RuntimeException("L'ingrédient '" + ingredient.getName() + "' existe déjà");
                    }
                }
            }
        }
    }




        public Dish saveDish(Dish dishToSave) throws SQLException {
        if (dishToSave == null) {
            throw new IllegalArgumentException("Le plat ne peut pas être null");
        }
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Démarrer une transaction
            
            Dish savedDish;
            
            if (dishToSave.getId() == 0) {
                // INSERT - Nouveau plat
                savedDish = insertDish(conn, dishToSave);
            } else {
                // UPDATE - Plat existant
                savedDish = updateDish(conn, dishToSave);
            }
            
            // Gérer les associations d'ingrédients
            updateDishIngredients(conn, savedDish);
            
            conn.commit(); // Valider la transaction
            return savedDish;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Annuler en cas d'erreur
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Ignorer
                }
            }
        }
    }

    private Dish insertDish(Connection conn, Dish dish) throws SQLException {
        String sql = "INSERT INTO dish (name, type) VALUES (?, ?) RETURNING id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDishTypeEnum().name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    return new Dish(newId, dish.getName(), dish.getDishTypeEnum(), dish.getIngredient(), 0.0);
                }
            }
        }
        throw new SQLException("Échec de l'insertion du plat");
    }

    private Dish updateDish(Connection conn, Dish dish) throws SQLException {
        String sql = "UPDATE dish SET name = ?, type = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dish.getName());
            stmt.setString(2, dish.getDishTypeEnum().name());
            stmt.setInt(3, dish.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun plat trouvé avec l'ID: " + dish.getId());
            }
        }
        return dish;
    }

    private void updateDishIngredients(Connection conn, Dish dish) throws SQLException {
        // 1. Dissocier tous les ingrédients actuels de ce plat
        String dissociateSql = "UPDATE ingredient SET id_dish = NULL WHERE id_dish = ?";
        try (PreparedStatement stmt = conn.prepareStatement(dissociateSql)) {
            stmt.setInt(1, dish.getId());
            stmt.executeUpdate();
        }
        
        // 2. Associer les nouveaux ingrédients
        if (dish.getIngredient() != null && !dish.getIngredient().isEmpty()) {
            String associateSql = "UPDATE ingredient SET id_dish = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(associateSql)) {
                for (Ingredient ingredient : dish.getIngredient()) {
                    stmt.setInt(1, dish.getId());
                    stmt.setInt(2, ingredient.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }




        public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Dish> dishes = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "SELECT DISTINCT d.id, d.name, d.type " +
                        "FROM dish d " +
                        "JOIN ingredient i ON d.id = i.id_dish " +
                        "WHERE LOWER(i.name) LIKE LOWER(?) " +
                        "ORDER BY d.id";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + ingredientName + "%");
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Dish dish = new Dish(
                            rs.getInt("id"),
                            rs.getString("name"),
                            Dish.dishType.valueOf(rs.getString("type")), null, 0.0
                        );
                        
                        // Charger les ingrédients pour ce plat
                        loadIngredientsForDish(conn, dish);
                        dishes.add(dish);
                    }
                }
            }
        }
        
        return dishes;
    }

    private void loadIngredientsForDish(Connection conn, Dish dish) throws SQLException {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id_dish = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dish.getId());
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<Ingredient> ingredients = new ArrayList<>();
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        Ingredient.CategoryEnum.valueOf(rs.getString("category")),
                        dish
                    );
                    ingredients.add(ingredient);
                }
            }
        }
    }



        public List<Ingredient> findIngredientsByCriteria(String ingredientName, Ingredient.Category category, 
                                                    String dishName, int page, int size) throws SQLException {
        if (page < 1) throw new IllegalArgumentException("Page doit être >= 1");
        if (size <= 0) throw new IllegalArgumentException("Size doit être > 0");
        
        int offset = (page - 1) * size;
        List<Ingredient> ingredients = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection()) {
            // Construction dynamique de la requête
            StringBuilder sqlBuilder = new StringBuilder(
                "SELECT i.id, i.name, i.price, i.category, " +
                "d.id as dish_id, d.name as dish_name, d.type as dish_type " +
                "FROM ingredient i " +
                "LEFT JOIN dish d ON i.id_dish = d.id " +
                "WHERE 1=1"
            );
            
            List<Object> parameters = new ArrayList<>();
            
            // Ajout des conditions dynamiquement
            if (ingredientName != null && !ingredientName.trim().isEmpty()) {
                sqlBuilder.append(" AND LOWER(i.name) LIKE LOWER(?)");
                parameters.add("%" + ingredientName.trim() + "%");
            }
            
            if (category != null) {
                sqlBuilder.append(" AND i.category = ?");
                parameters.add(category.toDatabase());
            }
            
            if (dishName != null && !dishName.trim().isEmpty()) {
                sqlBuilder.append(" AND LOWER(d.name) LIKE LOWER(?)");
                parameters.add("%" + dishName.trim() + "%");
            }
            
            // Ajout de l'ordre et de la pagination
            sqlBuilder.append(" ORDER BY i.id LIMIT ? OFFSET ?");
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString())) {
                // Set des paramètres dynamiques
                int paramIndex = 1;
                for (Object param : parameters) {
                    stmt.setObject(paramIndex++, param);
                }
                
                // Set des paramètres de pagination
                stmt.setInt(paramIndex++, size);
                stmt.setInt(paramIndex, offset);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Dish dish = null;
                        
                        if (rs.getObject("dish_id") != null) {
                            dish = new Dish(
                                rs.getInt("dish_id"),
                                rs.getString("dish_name"),
                                Dish.DishType.fromDatabase(rs.getString("dish_type"))
                            );
                        }
                        
                        Ingredient ingredient = new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            Ingredient.Category.fromDatabase(rs.getString("category")),
                            dish
                        );
                        
                        ingredients.add(ingredient);
                    }
                }
            }
        }
        
        return ingredients;
    }

}