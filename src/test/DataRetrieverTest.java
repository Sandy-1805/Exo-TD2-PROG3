public class DataRetrieverTest {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();
        
        try {
            // Test a) Dish findDishById(Integer id); id = 1
            System.out.println("Test a) findDishById avec id = 1");
            System.out.println("Résultat attendu : Salade Fraîche avec 2 ingrédients (Laitue et Tomate)\n");
            
            Dish dish = retriever.findDishById(1);
            
            if (dish == null) {
                System.out.println("ECHEC: Aucun plat trouvé avec id=1");
                return;
            }
            
            // Vérifications
            boolean testReussi = true;
            
            // 1. Vérifier le nom
            if (!"Salade fraiche".equals(dish.getName())) {
                System.out.println("Nom incorrect: " + dish.getName() + " au lieu de 'Salade fraiche'");
                testReussi = false;
            } else {
                System.out.println("Nom correct: " + dish.getName());
            }
            
            // 2. Vérifier le type
            if (dish.getType() != Dish.DishType.STARTER) {
                System.out.println("Type incorrect: " + dish.getType());
                testReussi = false;
            } else {
                System.out.println("✓ Type correct: " + dish.getType());
            }
            
            // 3. Vérifier le nombre d'ingrédients
            if (dish.getIngredients() == null || dish.getIngredients().size() != 2) {
                System.out.println("Nombre d'ingrédients incorrect: " + 
                    (dish.getIngredients() == null ? 0 : dish.getIngredients().size()));
                testReussi = false;
            } else {
                System.out.println("Nombre d'ingrédients correct: " + dish.getIngredients().size());
            }
            
            // 4. Vérifier les noms des ingrédients
            if (dish.getIngredients() != null) {
                boolean hasLaitue = false;
                boolean hasTomate = false;
                
                for (Ingredient ing : dish.getIngredients()) {
                    if ("Laitue".equals(ing.getName())) hasLaitue = true;
                    if ("Tomate".equals(ing.getName())) hasTomate = true;
                }
                
                if (!hasLaitue) {
                    System.out.println("Ingrédient manquant: Laitue");
                    testReussi = false;
                } else {
                    System.out.println("Ingrédient présent: Laitue");
                }
                
                if (!hasTomate) {
                    System.out.println("Ingrédient manquant: Tomate");
                    testReussi = false;
                } else {
                    System.out.println("Ingrédient présent: Tomate");
                }
                
                // Afficher les détails des ingrédients
                System.out.println("\nDétails des ingrédients:");
                for (Ingredient ing : dish.getIngredients()) {
                    System.out.println("  - " + ing.getName() + 
                                     " (Prix: " + ing.getPrice() + 
                                     ", Quantité: " + ing.getRequiredQuantity() + 
                                     ", Catégorie: " + ing.getCategory() + ")");
                }
            }
            
            // 5. Tenter de calculer le coût
            try {
                Double cost = dish.getDishCost();
                System.out.println("\n✓ Coût calculable: " + cost);
            } catch (RuntimeException e) {
                System.out.println("\nCoût non calculable: " + e.getMessage());
            }
            
            // Résumé du test
            System.out.println("\n" + (testReussi ? "TEST RÉUSSI" : "TEST ÉCHOUÉ"));
            
        } catch (SQLException e) {
            System.err.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }




    public static void main(String[] args) {   
        DataRetriever retriever = new DataRetriever();
        
        try {
            Dish dish = retriever.findDishById(999);
            
            if (dish == null) {
                System.out.println("TEST RÉUSSI: Méthode a retourné null (comportement accepté)");
                System.out.println("Note: Certaines implémentations retournent null, d'autres lèvent une exception");
            } else {
                System.out.println("TEST ÉCHOUÉ: A retourné un plat au lieu de null/exception");
                System.out.println("Plat retourné: " + dish.getName());
            }
            
        } catch (RuntimeException e) {
            System.out.println("EST RÉUSSI: RuntimeException levée");
            System.out.println("Message: " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println("SQLException levée au lieu de RuntimeException");
            System.out.println("Message: " + e.getMessage());
            
        } catch (Exception e) {
            System.out.println("Exception inattendue: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
        }
    }




    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();
        
        try {
            List<Ingredient> ingredients = retriever.findIngredients(2, 2);
            
            if (ingredients == null) {
                System.out.println("ECHEC: La liste est null");
                return;
            }
            
            System.out.println("Nombre d'ingredients retournes: " + ingredients.size());
            
            if (ingredients.size() != 2) {
                System.out.println("ECHEC: Attendu 2 ingredients, recu " + ingredients.size());
                System.out.println("Ingredients recus:");
                for (Ingredient ing : ingredients) {
                    System.out.println("  - " + ing.getName());
                }
                return;
            }
            
            String nom1 = ingredients.get(0).getName();
            String nom2 = ingredients.get(1).getName();
            
            boolean hasPoulet = "Poulet".equals(nom1) || "Poulet".equals(nom2);
            boolean hasChocolat = "Chocolat".equals(nom1) || "Chocolat".equals(nom2);
            
            if (hasPoulet && hasChocolat) {
                System.out.println("SUCCES: Les ingredients sont Poulet et Chocolat");
                System.out.println("  - " + nom1 + " (position 1)");
                System.out.println("  - " + nom2 + " (position 2)");
            } else {
                System.out.println("ECHEC: Ingredients incorrects");
                System.out.println("Attendu: Poulet et Chocolat");
                System.out.println("Recu: " + nom1 + " et " + nom2);
            }
            
            System.out.println("\nDetails des ingredients pagines:");
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ing = ingredients.get(i);
                System.out.println((i+1) + ". " + ing.getName() + 
                                 " (ID: " + ing.getId() + 
                                 ", Prix: " + ing.getPrice() + 
                                 ", Categorie: " + ing.getCategory() + ")");
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("ECHEC: IllegalArgumentException");
            System.out.println("Message: " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
            
        } catch (Exception e) {
            System.out.println("ERREUR INATTENDUE: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {      
        DataRetriever retriever = new DataRetriever();
        
        try {
            List<Ingredient> ingredients = retriever.findIngredients(3, 5);
            
            if (ingredients == null) {
                System.out.println("ERREUR: La methode a retourne null au lieu d'une liste vide");
                System.out.println("La methode devrait toujours retourner une liste (meme vide)");
                return;
            }
            
            System.out.println("Nombre d'ingredients retournes: " + ingredients.size());
            
            if (ingredients.isEmpty()) {
                System.out.println("SUCCES: La liste est vide comme attendu");
                
                System.out.println("\nExplication:");
                System.out.println("Total d'ingredients dans la base: 5");
                System.out.println("Taille de page (size): 5");
                System.out.println("Page 1: ingredients 1 a 5");
                System.out.println("Page 2: ingredients 6 a 10 (vide car seulement 5 ingredients)");
                System.out.println("Page 3: ingredients 11 a 15 (vide car seulement 5 ingredients)");
                
            } else {
                System.out.println("ECHEC: La liste n'est pas vide");
                System.out.println("Ingredients trouves:");
                for (Ingredient ing : ingredients) {
                    System.out.println("  - " + ing.getName() + " (ID: " + ing.getId() + ")");
                }
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("ERREUR: IllegalArgumentException");
            System.out.println("Message: " + e.getMessage());
            
        } catch (SQLException e) {
            System.out.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
            
        } catch (Exception e) {
            System.out.println("ERREUR INATTENDUE: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
        }
    }
}