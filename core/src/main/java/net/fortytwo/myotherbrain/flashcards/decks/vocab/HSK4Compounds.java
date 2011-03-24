package net.fortytwo.myotherbrain.flashcards.decks.vocab;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: josh
 * Date: 3/19/11
 * Time: 8:20 PM
 */
public class HSK4Compounds extends VocabularyDeck {

    public HSK4Compounds() throws IOException {
        super("hsk4_compounds");
    }

    @Override
    public Dictionary createVocabulary() throws IOException {
        Dictionary dict = new Dictionary();

        VocabularySource source = new VocabularySource("renzhe's lists");
        source.setComment("HSK level 4 vocabulary lists compiled by user renzhe");
        source.setUrl("http://www.chinese-forums.com/index.php?/topic/14829-hsk-character-lists/");
        source.setTimestamp("2011-3-19");

        InputStream is = HSK4Characters.class.getResourceAsStream("hsk4-multitab.txt");
        try {
            VocabularyParsers.parseHSK4List(is, dict, source);
        } finally {
            is.close();
        }

        return dict;
    }
}