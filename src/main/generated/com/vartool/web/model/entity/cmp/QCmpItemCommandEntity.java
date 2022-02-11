package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCmpItemCommandEntity is a Querydsl query type for CmpItemCommandEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCmpItemCommandEntity extends EntityPathBase<CmpItemCommandEntity> {

    private static final long serialVersionUID = 302044813L;

    public static final QCmpItemCommandEntity cmpItemCommandEntity = new QCmpItemCommandEntity("cmpItemCommandEntity");

    public final QCmpEntity _super = new QCmpEntity(this);

    public final StringPath cmdCharset = createString("cmdCharset");

    //inherited
    public final StringPath cmpId = _super.cmpId;

    //inherited
    public final StringPath cmpType = _super.cmpType;

    //inherited
    public final StringPath description = _super.description;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public final StringPath startCmd = createString("startCmd");

    public final StringPath stopCmd = createString("stopCmd");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public QCmpItemCommandEntity(String variable) {
        super(CmpItemCommandEntity.class, forVariable(variable));
    }

    public QCmpItemCommandEntity(Path<? extends CmpItemCommandEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCmpItemCommandEntity(PathMetadata metadata) {
        super(CmpItemCommandEntity.class, metadata);
    }

}

