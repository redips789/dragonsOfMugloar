package com.dragons.fixture;

import com.dragons.model.Item;
import com.dragons.model.Message;

import java.util.List;

public class FixtureFactory {

    public static List<Message> getMessages() {
        return List.of(
                new Message("YbQYYtut", "Help Asaf Ásmundsson to sell an unordinary clothes on the local market", 21, 7, "Piece of cake"),
                new Message("A4eYlPOp", "Help Nasrin Baldwin to transport a magic chariot to village in Highheart", 13, 7, "Hmmm...."),
                new Message("vctgTFTz", "Help Pravina Bissette to fix their sheep", 5, 7, "Piece of cake"),
                new Message("PUVT64or", "Help Macy Plonta to clean their dog", 7, 7, "Sure thing"),
                new Message("rzw7JkNs", "Create an advertisement campaign for Kenyon Pearson to promote their horse based business", 30, 7, "Hmmm...."),
                new Message("AKw1OCph", "Help Isobel Sessions to clean their chariot", 7, 7, "Walk in the park"),
                new Message("8AZ0x83P", "Help Kelila Richard to fix their sheep", 6, 7, "Sure thing"),
                new Message("gzponC7R", "Help Rhian Savidge to write their biographical novel about their difficulties with a deranged cat", 34, 7, "Risky"),
                new Message("B7J7NXFG", "Help Audie Normanson to write their biographical novel about their difficulties with a deranged house", 45, 7, "Risky"),
                new Message("a5kTy5Oh", "Help Shri Edwardson to reach an agreement with Randy Clayton on the matters of disputed horse", 16, 7, "Sure thing"));
    }

    public static List<Item> getItems() {
        return List.of(
                new Item("hpot", "Healing potion", 50),
                new Item("cs", "Claw Sharpening", 100),
                new Item("gas", "Gasoline", 100),
                new Item("wax", "Copper Plating", 100),
                new Item("tricks", "Book of Tricks", 100),
                new Item("wingpot", "Potion of Stronger Wings", 100),
                new Item("ch", "Claw Honing", 300),
                new Item("rf", "Rocket Fuel", 300),
                new Item("iron", "Iron Plating", 300),
                new Item("mtrix", "Book of Megatricks", 300),
                new Item("wingpotmax", "Potion of Awesome Wings", 300)
        );
    }
}
