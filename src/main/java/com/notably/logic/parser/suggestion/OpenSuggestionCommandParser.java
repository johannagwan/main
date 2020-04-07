package com.notably.logic.parser.suggestion;

import static com.notably.logic.parser.CliSyntax.PREFIX_TITLE;

import com.notably.commons.path.AbsolutePath;
import com.notably.logic.commands.suggestion.OpenSuggestionCommand;
import com.notably.logic.correction.CorrectionEngine;
import com.notably.logic.correction.CorrectionResult;
import com.notably.logic.correction.CorrectionStatus;
import com.notably.logic.parser.ArgumentMultimap;
import com.notably.logic.parser.ArgumentTokenizer;
import com.notably.logic.parser.ParserUtil;
import com.notably.logic.parser.exceptions.ParseException;
import com.notably.model.Model;

/**
 * Represents a Parser for OpenSuggestionCommand.
 */
public class OpenSuggestionCommandParser implements SuggestionCommandParser<OpenSuggestionCommand> {
    private Model model;
    private CorrectionEngine<AbsolutePath> pathCorrectionEngine;

    public OpenSuggestionCommandParser(Model model, CorrectionEngine<AbsolutePath> pathCorrectionEngine) {
        this.model = model;
        this.pathCorrectionEngine = pathCorrectionEngine;
    }

    /**
     * Parses user input in the context of the OpenSuggestionCommand.
     * @param userInput The user's input.
     * @return An OpenSuggestionCommand object with a corrected absolute path.
     * @throws ParseException if the user input is in a wrong format and/ or path cannot be found.
     */
    @Override
    public OpenSuggestionCommand parse(String userInput) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(userInput, PREFIX_TITLE);

        String title;
        if (!ParserUtil.arePrefixesPresent(argMultimap, PREFIX_TITLE)
                || !argMultimap.getPreamble().isEmpty()) {
            title = userInput.trim();
        } else {
            title = argMultimap.getValue(PREFIX_TITLE).get();
        }

        AbsolutePath uncorrectedPath;
        try {
            uncorrectedPath = ParserUtil.createAbsolutePath(title, model.getCurrentlyOpenPath());
        } catch (ParseException pe) {
            throw new ParseException("Cannot open \"" + title + "\". Invalid path.");
        }

        CorrectionResult<AbsolutePath> correctionResult = pathCorrectionEngine.correct(uncorrectedPath);
        if (correctionResult.getCorrectionStatus() == CorrectionStatus.FAILED) {
            throw new ParseException("Cannot open \"" + title + "\". Invalid path.");
        }

        // TODO: Pass in the list of corrected items and create suggestions based on that
        return new OpenSuggestionCommand(correctionResult.getCorrectedItems().get(0), title);
    }
}
