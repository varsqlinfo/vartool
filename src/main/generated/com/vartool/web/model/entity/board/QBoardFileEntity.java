package com.vartool.web.model.entity.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardFileEntity is a Querydsl query type for BoardFileEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBoardFileEntity extends EntityPathBase<BoardFileEntity> {

    private static final long serialVersionUID = 1034944045L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardFileEntity boardFileEntity = new QBoardFileEntity("boardFileEntity");

    public final com.vartool.web.model.entity.QFileBaseEntity _super = new com.vartool.web.model.entity.QFileBaseEntity(this);

    public final QBoardEntity article;

    public final StringPath boardCode = createString("boardCode");

    public final QBoardCommentEntity comment;

    public final NumberPath<Long> contId = createNumber("contId", Long.class);

    public final StringPath contType = createString("contType");

    //inherited
    public final StringPath fileExt = _super.fileExt;

    //inherited
    public final StringPath fileFieldName = _super.fileFieldName;

    public final NumberPath<Long> fileId = createNumber("fileId", Long.class);

    //inherited
    public final StringPath fileName = _super.fileName;

    //inherited
    public final StringPath filePath = _super.filePath;

    //inherited
    public final NumberPath<Long> fileSize = _super.fileSize;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public QBoardFileEntity(String variable) {
        this(BoardFileEntity.class, forVariable(variable), INITS);
    }

    public QBoardFileEntity(Path<? extends BoardFileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardFileEntity(PathMetadata metadata, PathInits inits) {
        this(BoardFileEntity.class, metadata, inits);
    }

    public QBoardFileEntity(Class<? extends BoardFileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new QBoardEntity(forProperty("article")) : null;
        this.comment = inits.isInitialized("comment") ? new QBoardCommentEntity(forProperty("comment"), inits.get("comment")) : null;
    }

}

