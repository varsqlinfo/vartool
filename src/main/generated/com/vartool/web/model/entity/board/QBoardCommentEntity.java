package com.vartool.web.model.entity.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardCommentEntity is a Querydsl query type for BoardCommentEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBoardCommentEntity extends EntityPathBase<BoardCommentEntity> {

    private static final long serialVersionUID = -2011763020L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardCommentEntity boardCommentEntity = new QBoardCommentEntity("boardCommentEntity");

    public final com.vartool.web.model.entity.base.QAabstractAuditorModel _super = new com.vartool.web.model.entity.base.QAabstractAuditorModel(this);

    public final NumberPath<Long> articleId = createNumber("articleId", Long.class);

    public final StringPath authorName = createString("authorName");

    public final StringPath boardCode = createString("boardCode");

    public final ListPath<BoardCommentEntity, QBoardCommentEntity> children = this.<BoardCommentEntity, QBoardCommentEntity>createList("children", BoardCommentEntity.class, QBoardCommentEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    public final StringPath contents = createString("contents");

    public final BooleanPath delYn = createBoolean("delYn");

    public final ListPath<BoardFileEntity, QBoardFileEntity> fileList = this.<BoardFileEntity, QBoardFileEntity>createList("fileList", BoardFileEntity.class, QBoardFileEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> grpCommentId = createNumber("grpCommentId", Long.class);

    public final NumberPath<Long> grpSeq = createNumber("grpSeq", Long.class);

    public final NumberPath<Integer> indent = createNumber("indent", Integer.class);

    public final QBoardCommentEntity parent;

    public final NumberPath<Long> parentCommentId = createNumber("parentCommentId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public QBoardCommentEntity(String variable) {
        this(BoardCommentEntity.class, forVariable(variable), INITS);
    }

    public QBoardCommentEntity(Path<? extends BoardCommentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardCommentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardCommentEntity(PathMetadata metadata, PathInits inits) {
        this(BoardCommentEntity.class, metadata, inits);
    }

    public QBoardCommentEntity(Class<? extends BoardCommentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parent = inits.isInitialized("parent") ? new QBoardCommentEntity(forProperty("parent"), inits.get("parent")) : null;
    }

}

