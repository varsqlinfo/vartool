package com.vartool.web.model.entity.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardEntity is a Querydsl query type for BoardEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBoardEntity extends EntityPathBase<BoardEntity> {

    private static final long serialVersionUID = -812371823L;

    public static final QBoardEntity boardEntity = new QBoardEntity("boardEntity");

    public final com.vartool.web.model.entity.base.QAabstractAuditorModel _super = new com.vartool.web.model.entity.base.QAabstractAuditorModel(this);

    public final NumberPath<Long> articleId = createNumber("articleId", Long.class);

    public final StringPath authorName = createString("authorName");

    public final StringPath boardCode = createString("boardCode");

    public final NumberPath<Long> commentCnt = createNumber("commentCnt", Long.class);

    public final StringPath contents = createString("contents");

    public final ListPath<BoardFileEntity, QBoardFileEntity> fileList = this.<BoardFileEntity, QBoardFileEntity>createList("fileList", BoardFileEntity.class, QBoardFileEntity.class, PathInits.DIRECT2);

    public final ComparablePath<Character> noticeYn = createComparable("noticeYn", Character.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public QBoardEntity(String variable) {
        super(BoardEntity.class, forVariable(variable));
    }

    public QBoardEntity(Path<? extends BoardEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardEntity(PathMetadata metadata) {
        super(BoardEntity.class, metadata);
    }

}

