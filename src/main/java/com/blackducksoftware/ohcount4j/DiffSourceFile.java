/*
 * Copyright 2016 Black Duck Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blackducksoftware.ohcount4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.blackducksoftware.ohcount4j.detect.Detector;
import com.blackducksoftware.ohcount4j.scan.Line;
import com.blackducksoftware.ohcount4j.scan.LineHandler;

import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * @author mpujari
 *
 */
public class DiffSourceFile {

    public Diff diff(SourceFile from, SourceFile to) throws IOException {
        Language fromLanguage = Detector.detect(from);
        Language toLanguage = Detector.detect(to);

        LineDetailHandler fromLinehandler = new LineDetailHandler();
        fromLanguage.makeScanner().scan(from, fromLinehandler);

        LineDetailHandler toLinehandler = new LineDetailHandler();
        toLanguage.makeScanner().scan(to, toLinehandler);

        List<String> original = fromLinehandler.contentList;
        List<String> revised = toLinehandler.contentList;

        // Compute diff. Get the Patch object. Patch is the container for computed deltas.
        Patch<String> patch = DiffUtils.diff(original, revised);

        MutableInt cAdded = new MutableInt(0); // code added
        MutableInt cRemoved = new MutableInt(0); // code removed
        MutableInt bAdded = new MutableInt(0); // blank added
        MutableInt bRemoved = new MutableInt(0); // blank removed
        MutableInt cmtAdded = new MutableInt(0); // comment added
        MutableInt cmtRemoved = new MutableInt(0); // comment removed
        for (Delta<String> delta : patch.getDeltas()) {
            switch (delta.getType()) {
            case CHANGE: {
                // its considered as 1 is added and 1 is removed
                accountDiff(cAdded, cRemoved, bAdded, bRemoved,
                        cmtAdded, cmtRemoved, Delta.TYPE.DELETE, delta.getOriginal(), fromLinehandler);
                accountDiff(cAdded, cRemoved, bAdded, bRemoved,
                        cmtAdded, cmtRemoved, Delta.TYPE.INSERT, delta.getRevised(), toLinehandler);
                break;
            }
            case DELETE: {
                accountDiff(cAdded, cRemoved, bAdded, bRemoved,
                        cmtAdded, cmtRemoved, Delta.TYPE.DELETE, delta.getOriginal(), fromLinehandler);
                break;
            }
            case INSERT: {
                accountDiff(cAdded, cRemoved, bAdded, bRemoved,
                        cmtAdded, cmtRemoved, Delta.TYPE.INSERT, delta.getRevised(), toLinehandler);
                break;
            }
            }

        }
        return new Diff(cAdded.intValue(), cRemoved.intValue(),
                cmtAdded.intValue(), cmtRemoved.intValue(), bAdded.intValue(), bRemoved.intValue());
    }

    private void accountDiff(MutableInt codeAdded, MutableInt codeRemoved,
            MutableInt blankAdded, MutableInt blankRemoved, MutableInt commentAdded,
            MutableInt commentRemoved, TYPE type, Chunk<String> chunk, LineDetailHandler linehandler) {
        if (type == TYPE.CHANGE) {
            // CHANGE should be like INSERT and DELETE
            throw new IllegalArgumentException("TYPE.CHANGE should not be sent");
        }
        int position = chunk.getPosition();
        for (int i = 0; i < chunk.getLines().size(); i++) {
            Line line = linehandler.lineDetails.get(position + i);
            switch (line.getEntity()) {
            case CODE:
                if (type == TYPE.INSERT) {
                    codeAdded.increment();
                } else {
                    codeRemoved.increment();
                }
                break;
            case BLANK:
                if (type == TYPE.INSERT) {
                    blankAdded.increment();
                } else {
                    blankRemoved.increment();
                }
                break;
            case COMMENT:
                if (type == TYPE.INSERT) {
                    commentAdded.increment();
                } else {
                    commentRemoved.increment();
                }
                break;
            }
        }
    }

    private static class LineDetailHandler implements LineHandler {

        List<Line> lineDetails = new ArrayList<Line>();

        List<String> contentList = new ArrayList<String>();

        @Override
        public void handleLine(Line line) {
            lineDetails.add(line);
            contentList.add(line.getContent());
        }

    }

}
