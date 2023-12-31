package MyUnoGame.Cards;

import UnoGame.Cards.CardBase;
import UnoGame.Cards.Enums.CardAction;
import UnoGame.Cards.Enums.CardColor;
import UnoGame.Cards.Enums.CardType;

public class DanceCard extends CardBase {
    public static int count = 0;
    private String value;
    public DanceCard(){
        count++;
        value = "Dance" + count;
    }
    @Override
    public String Show() {
        return "🕺";
    }

    @Override
    public CardBase getCard() {
        return new DanceCard();
    }

    @Override
    public CardAction getAction() {
        return CardAction.NO_ACTION;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public CardColor getColor() {
        return CardColor.NO_COLOR;
    }

    @Override
    public CardType getType() {
        return CardType.WILD;
    }

    @Override
    public void setColor(CardColor color) {

    }
}
