package com.notably.logic.commands.suggestion;

import static com.notably.logic.parser.CliSyntax.PREFIX_TITLE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.notably.commons.path.AbsolutePath;
import com.notably.logic.suggestion.SuggestionTestUtil;
import com.notably.model.Model;
import com.notably.model.suggestion.SuggestionItem;

public class OpenSuggestionCommandTest {
    private static AbsolutePath toCs2103t;
    private static List<AbsolutePath> paths;
    private static Model model;

    private static final String COMMAND_WORD = "open";

    @BeforeAll
    public static void setUp() {
        toCs2103t = SuggestionTestUtil.getToCs2103t();
        paths = new ArrayList<>();
        paths.add(toCs2103t);
        model = SuggestionTestUtil.getModel();
    }

    @AfterEach
    public void clearSuggestions() {
        model.clearSuggestions();
    }

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new OpenSuggestionCommand(null, "title"));
    }

    @Test
    public void constructor_nullTitle_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new OpenSuggestionCommand(paths,
                null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        OpenSuggestionCommand openSuggestionCommand = new OpenSuggestionCommand(paths,
                toCs2103t.getStringRepresentation());
        assertThrows(NullPointerException.class, () -> openSuggestionCommand.execute(null));
    }

    @Test
    public void execute_blankOldTitle_generatesEmptySuggestion() {
        OpenSuggestionCommand openSuggestionCommand = new OpenSuggestionCommand(paths, "    ");
        openSuggestionCommand.execute(model);

        assertTrue(model.getSuggestions().size() == 0);
    }

    @Test
    public void execute_correctAbsolutePathWithPrefix_generatesResponseCorrectly() {
        String userInputWithoutPath = COMMAND_WORD + " " + PREFIX_TITLE + " ";
        model.setInput(userInputWithoutPath + toCs2103t.getStringRepresentation());
        OpenSuggestionCommand openSuggestionCommand = new OpenSuggestionCommand(paths,
                toCs2103t.getStringRepresentation());
        openSuggestionCommand.execute(model);

        List<SuggestionItem> suggestions = model.getSuggestions();

        // Expected suggestions
        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSugForCs2103tPathInput();

        // Test suggestions
        SuggestionTestUtil.testSuggestions(expectedSuggestions, suggestions);

        // Expected inputs
        List<String> expectedInputs = SuggestionTestUtil.getExpectedInputsForCs2103tPathInput(userInputWithoutPath);

        // Test inputs
        SuggestionTestUtil.testInputs(expectedInputs, suggestions, model);
    }

    @Test
    public void execute_correctAbsolutePathWithoutPrefix_generatesResponseCorrectly() {
        String userInputWithoutPath = COMMAND_WORD + " ";
        model.setInput(userInputWithoutPath + toCs2103t.getStringRepresentation());
        OpenSuggestionCommand openSuggestionCommand = new OpenSuggestionCommand(paths,
                toCs2103t.getStringRepresentation());
        openSuggestionCommand.execute(model);

        List<SuggestionItem> suggestions = model.getSuggestions();

        // Expected suggestions
        List<SuggestionItem> expectedSuggestions = SuggestionTestUtil.getExpectedSugForCs2103tPathInput();

        // Test suggestions
        SuggestionTestUtil.testSuggestions(expectedSuggestions, suggestions);

        // Expected inputs
        List<String> expectedInputs = SuggestionTestUtil.getExpectedInputsForCs2103tPathInput(userInputWithoutPath);

        // Test inputs
        SuggestionTestUtil.testInputs(expectedInputs, suggestions, model);
    }
}
