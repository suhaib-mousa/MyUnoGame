package MyUnoGame;

import MyUnoGame.Engines.BasicVersionUnoEngine;
import UnoGame.Decks.AllBlanksUsedException;
import UnoGame.Engines.BaseUnoEngine;
import UnoGame.Players.PlayerFacade;

import java.util.List;

public class Main {
    public static void main(String[] arg){
        BaseUnoEngine unoEngine = new BasicVersionUnoEngine();
        PlayerFacade playerFacade = new PlayerFacade(unoEngine);

        // Add players using the PlayerFacade
        playerFacade.addPlayers(List.of(
                "Player 1", "Player 2"
        ));

        try {
            // Start the Uno game
            unoEngine.play();
        } catch (AllBlanksUsedException e) {
            System.out.println("All blank cards used. Game initialization error.");
        }
    }
}
