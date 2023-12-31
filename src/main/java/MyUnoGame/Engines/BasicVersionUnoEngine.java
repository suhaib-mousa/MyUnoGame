package MyUnoGame.Engines;


import UnoGame.Cards.CardBase;
import UnoGame.Cards.Enums.CardColor;
import UnoGame.Cards.Enums.CardType;
import UnoGame.Decks.AllBlanksUsedException;
import UnoGame.Decks.Deck;
import UnoGame.Engines.BaseUnoEngine;
import UnoGame.Players.Player;

import java.util.*;

public class BasicVersionUnoEngine extends BaseUnoEngine {
    private List<CardBase> drawPile;
    private List<Player> players = getPlayers();
    private List<CardBase> discardPile;
    private Map<Player, List<CardBase>> playerHands;
    private int currentPlayerIndex;
    private boolean directionClockwise = true;
    List<CardBase> blanks = new ArrayList<>();

    @Override
    protected void dealingCardsToPlayers() throws AllBlanksUsedException {
        drawPile = new ArrayList<>(); // Initialize draw pile
        discardPile = new ArrayList<>(); // Initialize discard pile
        playerHands = new HashMap<>(); // Initialize player hands
        // Prepare the deck
        Deck deck = Deck.getInstance(this.blanks);
        deck.shuffle();

        System.out.println("Deal 7 cards for each player");
        for (Player player : getPlayers()) {
            System.out.print(player.getName() + ": ");
            List<CardBase> hand = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                CardBase card = deck.drawCard();
                System.out.print(card.Show() + ", ");
                hand.add(card);
            }
            System.out.println();

            playerHands.put(player, hand);
        }

        // Set up initial discard pile
        CardBase firstCard = deck.drawCard();
        System.out.println("First card: " + firstCard.Show());
        discardPile.add(firstCard);
        drawPile = deck.unoDeck;
    }
    public void setBlanks(List<CardBase> blanks) {
        this.blanks = blanks;
    }
    @Override
    protected boolean isGameFinished() {
        for (Player player : players) {
            if (playerHands.get(player).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void currentPlayerTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.print("Current Player: " + currentPlayer.getName() + " ");
        List<CardBase> currentPlayerHand = playerHands.get(currentPlayer);
        System.out.print("Cards in hand: ");
        for (CardBase card : currentPlayerHand) {
            System.out.print(card.Show() + " ");
        }
        System.out.println();
    }

    @Override
    protected boolean playerNeedsToDraw() {
        Player currentPlayer = players.get(currentPlayerIndex);
        List<CardBase> currentPlayerHand = playerHands.get(currentPlayer);
        for (CardBase card : currentPlayerHand) {
            if (isValidMove(card, discardPile.get(discardPile.size() - 1))) {
                return false;
            }
        }
        System.out.println(currentPlayer.getName() + " needs to draw");
        return true;
    }

    private boolean isValidMove(CardBase card, CardBase topDiscard) {
        if (card.getType() == CardType.WILD){
            return true;
        }
        if (card.getType() == CardType.NUMBER) {
            return card.getColor() == topDiscard.getColor()
                    || card.getValue().equals(topDiscard.getValue());
        }
        if (card.getType() == CardType.ACTION) {
            return card.getColor() == topDiscard.getColor()
                    ||card.getAction() == topDiscard.getAction();
        }
        return false;
    }


    @Override
    protected void drawCard() {
        Player currentPlayer = players.get(currentPlayerIndex);
        List<CardBase> currentPlayerHand = playerHands.get(currentPlayer);
        CardBase drawnCard = drawPile.remove(drawPile.size() - 1);
        currentPlayerHand.add(drawnCard);

        if (isValidMove(drawnCard, discardPile.get(discardPile.size() - 1))) {
            System.out.println("You've drawn a valid card: " + drawnCard.Show());
            System.out.println("Do you want to play this card? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String decision = scanner.nextLine().toLowerCase();

            if (decision.equals("yes")) {
                System.out.println(drawnCard.Show() + " Played");
                currentPlayerHand.remove(drawnCard);
                discardPile.add(drawnCard);
            }
        }
    }

    @Override
    protected CardBase playCard() {
        Player currentPlayer = players.get(currentPlayerIndex);
        List<CardBase> currentPlayerHand = playerHands.get(currentPlayer);

        // Display valid cards the player can play
        List<CardBase> validCards = getValidCards(currentPlayerHand);
        if (validCards.isEmpty()) {
            System.out.println("No valid cards to play. Drawing a card.");
            drawCard();
            return null;
        }

        // Prompt the player to choose a valid card to play
        System.out.println("Valid cards to play: ");
        for (int i = 0; i < validCards.size(); i++) {
            System.out.println((i + 1) + ". " + validCards.get(i).getCard().Show());
        }
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Choose a card (1-" + validCards.size() + "): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Choose a card (1-" + validCards.size() + "): ");
                scanner.next();
            }
            choice = scanner.nextInt();
        } while (choice < 1 || choice > validCards.size());

        CardBase cardToPlay = validCards.get(choice - 1);

        System.out.println(cardToPlay.Show() + " Played");
        // Remove the played card from the player's hand and add it to the discard pile
        currentPlayerHand.remove(cardToPlay);
        discardPile.add(cardToPlay);

        return cardToPlay;
    }
    // Method to get valid cards that can be played from the player's hand
    private List<CardBase> getValidCards(List<CardBase> currentPlayerHand) {
        List<CardBase> validCards = new ArrayList<>();
        CardBase topDiscard = discardPile.get(discardPile.size() - 1);

        for (CardBase card : currentPlayerHand) {
            if (isValidMove(card, topDiscard)) {
                validCards.add(card);
            }
        }
        return validCards;
    }
    @Override
    protected void displayWinner() {
        for (Player player : players) {
            if (playerHands.get(player).isEmpty()) {
                String goldColor = "\u001B[33m";
                String resetStyle = "\u001B[0m";
                System.out.println(goldColor + "🥇 " + player.getName() + " wins!🥇" + resetStyle);
                break;
            }
        }
    }
    @Override
    protected void handleSkipAction() {
        System.out.println("Skip action - Next player's turn is skipped!");
        changeTurn(); // Skip next player's turn
    }

    @Override
    protected  void handleReverseAction() {
        System.out.println("Reverse action - Change direction!");
        directionClockwise = !directionClockwise; // Reverse direction
    }

    @Override
    protected  void handleDrawTwoAction() throws AllBlanksUsedException {
        System.out.println("Draw Two action - Next player draws 2 cards!");
        drawCardsForNextPlayer(2); // Next player draws 2 cards
    }

    @Override
    protected  void handleChangeColorAction() {
        System.out.println("Change Color action - Choose a new color!");
        // Prompt the player to choose a new color
        System.out.println("Choose a color:");
        System.out.println("1. Red");
        System.out.println("2. Blue");
        System.out.println("3. Green");
        System.out.println("4. Yellow");

        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Enter the number for the desired color (1-4): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Enter the number for the desired color (1-4): ");
                scanner.next();
            }
            choice = scanner.nextInt();
        } while (choice < 1 || choice > 4);

        // Set the new color based on the player's choice
        CardBase topDiscard = discardPile.get(discardPile.size() - 1);
        CardColor newColor = switch (choice) {
            case 1 -> CardColor.RED;
            case 2 -> CardColor.BLUE;
            case 3 -> CardColor.GREEN;
            case 4 -> CardColor.YELLOW;
            default -> topDiscard.getColor();
        };
        topDiscard.setColor(newColor);
        System.out.println("New color chosen: " + newColor);
    }

    @Override
    protected  void handleChangeColorDrawFourAction() throws AllBlanksUsedException {
        System.out.println("Change Color Draw Four action - Choose a new color and next player draws 4 cards!");
        // Logic to allow the current player to choose a new color
        drawCardsForNextPlayer(4); // Next player draws 4 cards
    }

    @Override
    protected void performAdditionalActionCards(CardBase card) {

    }

    // Helper method to draw cards for the next player
    private void drawCardsForNextPlayer(int numCards) throws AllBlanksUsedException {
        int nextPlayerIndex = getNextPlayerIndex();
        Player nextPlayer = players.get(nextPlayerIndex);
        List<CardBase> nextPlayerHand = playerHands.get(nextPlayer);

        // Draw cards for the next player
        Deck deck = Deck.getInstance();
        for (int i = 0; i < numCards; i++) {
            nextPlayerHand.add(deck.drawCard());
        }
    }

    // Helper method to change the turn by a specified offset
    @Override
    public void changeTurn() {
        if (directionClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    // Helper method to get the index of the next player with a specified offset
    private int getNextPlayerIndex() {
        if (directionClockwise) {
            return (currentPlayerIndex + 1) % players.size();
        } else {
            return (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }
}
