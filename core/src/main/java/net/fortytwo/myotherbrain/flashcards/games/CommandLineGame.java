package net.fortytwo.myotherbrain.flashcards.games;

import net.fortytwo.myotherbrain.flashcards.Card;
import net.fortytwo.myotherbrain.flashcards.Deck;
import net.fortytwo.myotherbrain.flashcards.Game;
import net.fortytwo.myotherbrain.flashcards.GameplayException;
import net.fortytwo.myotherbrain.flashcards.Pile;
import net.fortytwo.myotherbrain.flashcards.PriorityPile;
import net.fortytwo.myotherbrain.flashcards.Trial;
import net.fortytwo.myotherbrain.flashcards.db.CloseableIterator;
import net.fortytwo.myotherbrain.flashcards.db.file.FileBasedGameHistory;
import net.fortytwo.myotherbrain.flashcards.db.GameHistory;
import net.fortytwo.myotherbrain.flashcards.decks.geo.InternationalBorders;
import net.fortytwo.myotherbrain.flashcards.decks.vocab.FrenchVocabulary;
import net.fortytwo.myotherbrain.flashcards.decks.vocab.HSK4Characters;
import net.fortytwo.myotherbrain.flashcards.decks.vocab.HSK4Compounds;
import net.fortytwo.myotherbrain.flashcards.decks.vocab.NPCRVocabulary;
import net.fortytwo.myotherbrain.flashcards.decks.geo.NationalCapitals;
import net.fortytwo.myotherbrain.flashcards.decks.geo.USStateBorders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * User: josh
 * Date: 3/14/11
 * Time: 11:58 AM
 */
public class CommandLineGame extends Game<String, String> {
    public CommandLineGame(final Pile<String, String> pile,
                           final GameHistory history) {
        super(pile, history);
    }

    private void showCardHistory(final Card c) {
        CloseableIterator<Trial> h = history.getHistory(c);
        try {
            while (h.hasNext()) {
                System.out.println("\t\t" + h.next().printPlainText());
            }
        } finally {
            h.close();
        }
    }

    private boolean tryCard(final Card c,
                            final BufferedReader br,
                            final int line) throws IOException {
        //PrintStream ps = System.out;
        PrintStream ps = new PrintStream(System.out, true, "UTF-8");
        ps.println(line + ") " + c.getQuestion() + "\n");

        while (true) {
            String input = br.readLine();
            if (0 < input.length()) {
                switch (input.charAt(0)) {
                    case 'r':
                        ps.println(c.getAnswer());
                        return true;
                    case 'w':
                        ps.println(c.getAnswer());
                        return false;
                    case 'p':
                        ps.println(c.getAnswer());
                        break;
                    case 'h':
                        showCardHistory(c);
                        break;
                    case 'i':
                        ps.println(showQueue());
                        break;
                    case 'q':
                        System.exit(0);
                        break;
                    default:
                        ps.print("(r)ight / (w)rong / (p)eek / (h)istory / (i)nfo / (q)uit ?  ");
                }
            }
        }
    }

    public void play() throws GameplayException {
        int line = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            Card<String, String> c = drawCard();
            try {
                long now = System.currentTimeMillis();

                if (tryCard(c, br, ++line)) {
                    correct(c, now);
                    history.log(new Trial(c.getDeck().getName(), c.getName(), now, Trial.Result.Correct));
                } else {
                    incorrect(c, now);
                    history.log(new Trial(c.getDeck().getName(), c.getName(), now, Trial.Result.Incorrect));
                }
            } catch (IOException e) {
                throw new GameplayException(e);
            }
            replaceCard(c);
        }
    }

    public static void main(final String[] args) {
        try {
            Deck<String, String> stateBorders = new USStateBorders();
            Deck<String, String> nationalCapitals = new NationalCapitals();
            Deck<String, String> internationalBorders = new InternationalBorders();
            Deck<String, String> npcrVocabulary = new NPCRVocabulary();
            Deck<String, String> hsk4Characters = new HSK4Characters();
            Deck<String, String> hsk4Compounds = new HSK4Compounds();
            Deck<String, String> frenchVocabulary = new FrenchVocabulary();

            //Pile<String, String> pile = new SingleDeckPile<String, String>(d);

            PriorityPile<String, String> pile = new PriorityPile<String, String>();
            //*
            //pile.addDeck(stateBorders, 1);
            pile.addDeck(nationalCapitals, 1);
            pile.addDeck(internationalBorders, 1);
            //pile.addDeck(npcrVocabulary, 4);
            pile.addDeck(hsk4Compounds, 2);
            pile.addDeck(hsk4Characters, 8);
            //*/
            pile.addDeck(frenchVocabulary, 10);

            GameHistory h = new FileBasedGameHistory(new File("/tmp/flashcards.txt"));

            new CommandLineGame(pile, h).play();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}