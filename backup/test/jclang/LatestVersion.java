

package jclang;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LatestVersion extends ClangTest {

    @NotNull
    private String getTestDeclarationsFile() {
        return "v16/include/clang-c/Index.h";
    }

    private void indexTestDeclarations(@NotNull IndexerCallback callback) {
        Index index = Clang.INSTANCE.createIndex(false, false);
        index.indexSourceFile(callback, getTestDeclarationsFile(), new String[]{});
    }


    @Test
    public void testIndexClangDeclaration() {
        indexTestDeclarations(new AbstractIndexerCallback() {
            @Override
            public void indexDeclaration(@NotNull DeclarationInfo info) {
                try {

                StringWriter sw = new StringWriter();
                final PrintWriter out = new PrintWriter(sw);
                Cursor cursor = info.getCursor();
                IndexLocation location = info.getLocation();
                out.print(location.getLine() + ":" + location.getColumn());
                out.print(" " + cursor.getKind().getSpelling());
                out.print(" " + cursor.getType().getKind());
                out.print(" " + nonEmptyCursorSpelling(cursor));
                out.println();

                EntityInfo entityInfo = info.getEntityInfo();
                out.println("  " + entityInfo.getUSR());
                out.println("  " + entityInfo.getKind());
                String spelling = cursor.getSpelling();
                assertEquals(spelling.isEmpty() ? null : spelling, entityInfo.getName());

                if (info.isRedeclaration()) out.println("  redecl");
                if (info.isDefinition()) out.println("  def");
                if (info.isContainer()) out.println("  cnt");
                if (info.isImplicit()) out.println("  implicit");
                out.close();
                System.out.println(sw);
                } catch (RuntimeException e) {
                    //e.printStackTrace();
                }
            }
        });
    }

    @NotNull
    private String nonEmptyCursorSpelling(@NotNull Cursor cursor) {
        String spelling = cursor.getSpelling();
        return spelling.isEmpty() ? "<no-name>" : spelling;
    }


}
