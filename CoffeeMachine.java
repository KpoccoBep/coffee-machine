package machine;

import java.util.Arrays;
import java.util.Scanner;

enum State {
    CHOOSING_ACTION,
    CHOOSING_DRINK,
    ADDING_SUPPLIES
}

class Machine {
    private int water;
    private int milk;
    private int coffee;
    private int cups;
    private int money;
    private State state;
    private int fill_counter;

    public Machine() {
        water = 400;
        milk = 540;
        coffee = 120;
        cups = 9;
        money = 550;
        state = State.CHOOSING_ACTION;
        fill_counter = 0;
    }

    public boolean showMessage() {
        switch (state) {
            case CHOOSING_ACTION:
                System.out.println();
                System.out.println("Write action (buy, fill, take, remaining, exit):");
                return true;

            case ADDING_SUPPLIES:
                switch (fill_counter) {
                    case 0:
                        System.out.println();
                        System.out.println("Write how many ml of water do you want to add:");
                        return true;
                    case 1:
                        System.out.println("Write how many ml of milk do you want to add:");
                        return true;
                    case 2:
                        System.out.println("Write how many grams of coffee beans do you want to add:");
                        return true;
                    case 3:
                        System.out.println("Write how many disposable cups of coffee do you want to add:");
                        return true;
                    default:
                        System.out.println();
                        System.out.println("Something went wrong - exiting...");
                        return false;
                }

            case CHOOSING_DRINK:
                System.out.println();
                System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:");
                return true;

            default:
                System.out.println();
                System.out.println("Something went wrong - exiting...");
                return false;
        }
    }

    public boolean processInput(String input) {
        switch (state) {
            case CHOOSING_ACTION:
                switch (input) {
                    case "buy":
                        state = State.CHOOSING_DRINK;
                        return true;

                    case "fill":
                        state = State.ADDING_SUPPLIES;
                        return true;

                    case "take":
                        System.out.println();
                        System.out.printf("I gave you $%d\n", money);
                        money = 0;
                        return true;

                    case "remaining":
                        printState();
                        return true;

                    case "exit":
                        return false;

                    default:
                        System.out.println();
                        System.out.println("Wrong action - must be one of following: buy, fill, take, remaining, exit");
                        return true;
                }
                
            case ADDING_SUPPLIES:
                switch (fill_counter) {
                    case 0:
                        water += Integer.parseUnsignedInt(input);
                        fill_counter++;
                        return true;

                    case 1:
                        milk += Integer.parseUnsignedInt(input);
                        fill_counter++;
                        return true;

                    case 2:
                        coffee += Integer.parseUnsignedInt(input);
                        fill_counter++;
                        return true;

                    case 3:
                        cups += Integer.parseUnsignedInt(input);
                        fill_counter = 0;
                        state = State.CHOOSING_ACTION;
                        return true;

                    default:
                        System.out.println();
                        System.out.println("Something went wrong - exiting...");
                        return false;
                }

            case CHOOSING_DRINK:
                if (input.equals("back")) {
                    state = State.CHOOSING_ACTION;
                    return true;
                }

                boolean isUnsignedInt = input.chars().allMatch(Character::isDigit);

                if (!isUnsignedInt) {
                    System.out.println();
                    System.out.println("Wrong input - must be one of following: 1 - espresso, 2 - latte, 3 - cappuccino");
                    return true;
                }

                int drink = Integer.parseUnsignedInt(input);

                if (drink < 1 || drink > 3) {
                    System.out.println();
                    System.out.println("Wrong drink - must be one of following: 1 - espresso, 2 - latte, 3 - cappuccino");
                    return true;
                }

                boolean[] enoughSupplies = checkSupplies(drink);

                if (all(enoughSupplies)) {
                    System.out.println();
                    System.out.println("I have enough resources, making you a coffee!");
                } else {
                    printWarning(enoughSupplies);
                    state = State.CHOOSING_ACTION;
                    return true;
                }

                boolean success = makeDrink(drink);

                if (success) {
                    state = State.CHOOSING_ACTION;
                    return true;
                } else {
                    System.out.println();
                    System.out.println("Something went wrong - exiting...");
                    return false;
                }

            default:
                System.out.println();
                System.out.println("Something went wrong - exiting...");
                return false;
        }
    }

    private void printState() {
        System.out.println();
        System.out.println("The coffee machine has:");
        System.out.printf("%d of water\n", water);
        System.out.printf("%d of milk\n", milk);
        System.out.printf("%d of coffee beans\n", coffee);
        System.out.printf("%d of disposable cups\n", cups);
        System.out.printf("%d of money\n", money);
    }

    private boolean[] checkSupplies(int drinkNumber) {
        boolean[] enoughSupplies = {true, true, true, true};

        switch (drinkNumber) {
            case 1: // Espresso
                enoughSupplies[0] = water >= 250;
                enoughSupplies[2] = coffee >= 16;
                break;
            case 2: // Latte
                enoughSupplies[0] = water >= 350;
                enoughSupplies[1] = milk >= 75;
                enoughSupplies[2] = coffee >= 20;
                break;
            case 3: // Cappuccino
                enoughSupplies[0] = water >= 200;
                enoughSupplies[1] = milk >= 100;
                enoughSupplies[2] = coffee >= 12;
                break;
            default:
                Arrays.fill(enoughSupplies, false);
                return enoughSupplies;
        }
        enoughSupplies[3] = cups >= 1;
        return enoughSupplies;
    }

    private void printWarning(boolean[] enoughSupplies) {
        String[] suppliesList = new String[enoughSupplies.length];
        int n = 0;
        for (int i = 0; i < enoughSupplies.length; i++) {
            if (!enoughSupplies[i]) {
                String supply;
                switch (i) {
                    case 0:
                        supply = "water";
                        break;
                    case 1:
                        supply = "milk";
                        break;
                    case 2:
                        supply = "coffee beans";
                        break;
                    case 3:
                        supply = "cups";
                        break;
                    default:
                        supply = "wrong supply";
                }
                suppliesList[n++] = supply;
            }
        }

        System.out.println();
        System.out.print("Sorry, not enough ");
        if (n == 1) {
            System.out.printf("%s!\n", suppliesList[0]);
        } else if (n == 2) {
            System.out.printf("%s and %s!\n", suppliesList[0], suppliesList[1]);
        } else {
            for (int i = 0; i < n - 2; i++) {
                System.out.printf("%s, ", suppliesList[i]);
            }
            System.out.printf("%s and %s!\n", suppliesList[n - 2], suppliesList[n - 1]);
        }
    }

    private boolean makeDrink(int drinkNumber) {
        switch (drinkNumber) {
            case 1: // Espresso
                water -= 250;
                coffee -= 16;
                money += 4;
                break;

            case 2: // Latte
                water -= 350;
                milk -= 75;
                coffee -= 20;
                money += 7;
                break;

            case 3: // Cappuccino
                water -= 200;
                milk -= 100;
                coffee -= 12;
                money += 6;
                break;

            default:
                return false;
        }
        cups--;
        return true;
    }

    /* private boolean any(boolean[] list) {
        for (boolean item : list) {
            if (item) { return true; }
        }
        return false;
    } */

    private boolean all(boolean[] list) {
        for (boolean item : list) {
            if (!item) { return false; }
        }
        return true;
    }
}

public class CoffeeMachine {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Machine machine = new Machine();

        boolean ok;
        String input;

        do {
            ok = machine.showMessage();
            input = scanner.nextLine();
            ok = ok && machine.processInput(input);
        } while (ok);
    }
}
