package MyUnoGame.Engines;

import MyUnoGame.Cards.DanceCard;
import UnoGame.Cards.CardBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DancingVersionUnoEngine extends BasicVersionUnoEngine{
    List<CardBase> blanks = new ArrayList<>(Arrays.asList(new DanceCard(), new DanceCard(), new DanceCard(), new DanceCard()));

    public DancingVersionUnoEngine(){
        setBlanks(blanks);
    }
    @Override
    public void setBlanks(List<CardBase> blanks) {
        super.setBlanks(blanks);
    }

    @Override
    protected void performAdditionalActionCards(CardBase card) {
        DanceCard danceCard = new DanceCard();
        if (card.Show().equals(danceCard.Show())){
            System.out.println("Next player should Dance!");
        }
    }
}
