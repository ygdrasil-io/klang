

package jclang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jclang.structs.CXIdxDeclInfo;

import java.util.Collections;
import java.util.List;

public class DeclarationInfo {
    private final EntityInfo entityInfo;
    private final Cursor cursor;
    private final IndexLocation location;
    private final ContainerInfo semanticContainer;
    private final ContainerInfo lexicalContainer;
    private final boolean isRedeclaration;
    private final boolean isDefinition;
    private final boolean isContainer;
    @Nullable
    private final ContainerInfo declAsContainer;
    private final boolean isImplicit;
    private final List<IndexAttribute> attributes;

    public DeclarationInfo(@NotNull CXIdxDeclInfo info) {
        this.entityInfo = new EntityInfo(info.entityInfo);
        this.cursor = new Cursor(info.cursor);
        this.location = new IndexLocation(info.loc);
        this.semanticContainer = new ContainerInfo(info.semanticContainer);
        this.lexicalContainer = new ContainerInfo(info.lexicalContainer);
        this.isRedeclaration = info.isRedeclaration;
        this.isDefinition = info.isDefinition;
        this.isContainer = info.isContainer;
        this.declAsContainer = info.declAsContainer == null ? null : new ContainerInfo(info.declAsContainer);
        this.isImplicit = info.isImplicit;
        this.attributes = IndexAttribute.createFromNative(info.attributes, info.numAttributes);
    }

    @NotNull
    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    @NotNull
    public Cursor getCursor() {
        return cursor;
    }

    @NotNull
    public IndexLocation getLocation() {
        return location;
    }

    @NotNull
    public ContainerInfo getSemanticContainer() {
        return semanticContainer;
    }

    @NotNull
    public ContainerInfo getLexicalContainer() {
        return lexicalContainer;
    }

    public boolean isRedeclaration() {
        return isRedeclaration;
    }

    public boolean isDefinition() {
        return isDefinition;
    }

    public boolean isContainer() {
        return isContainer;
    }

    @Nullable
    public ContainerInfo getDeclAsContainer() {
        return declAsContainer;
    }

    public boolean isImplicit() {
        return isImplicit;
    }

    @NotNull
    public List<IndexAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }
}
