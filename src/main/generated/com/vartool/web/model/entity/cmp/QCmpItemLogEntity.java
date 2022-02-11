package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QCmpItemLogEntity is a Querydsl query type for CmpItemLogEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCmpItemLogEntity extends EntityPathBase<CmpItemLogEntity> {

    private static final long serialVersionUID = -356150458L;

    public static final QCmpItemLogEntity cmpItemLogEntity = new QCmpItemLogEntity("cmpItemLogEntity");

    public final QCmpEntity _super = new QCmpEntity(this);

    //inherited
    public final StringPath cmpId = _super.cmpId;

    //inherited
    public final StringPath cmpType = _super.cmpType;

    //inherited
    public final StringPath description = _super.description;

    public final StringPath logCharset = createString("logCharset");

    public final StringPath logPath = createString("logPath");

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDt = _super.updDt;

    //inherited
    public final StringPath updId = _super.updId;

    public QCmpItemLogEntity(String variable) {
        super(CmpItemLogEntity.class, forVariable(variable));
    }

    public QCmpItemLogEntity(Path<? extends CmpItemLogEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCmpItemLogEntity(PathMetadata metadata) {
        super(CmpItemLogEntity.class, metadata);
    }

}

