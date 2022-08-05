package com.vartool.web.model.entity.cmp;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCmpGroupMappingEntity is a Querydsl query type for CmpGroupMappingEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCmpGroupMappingEntity extends EntityPathBase<CmpGroupMappingEntity> {

    private static final long serialVersionUID = 1160843392L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCmpGroupMappingEntity cmpGroupMappingEntity = new QCmpGroupMappingEntity("cmpGroupMappingEntity");

    public final com.vartool.web.model.entity.base.QAbstractRegAuditorModel _super = new com.vartool.web.model.entity.base.QAbstractRegAuditorModel(this);

    public final QCmpEntity cmpEntity;

    public final QCmpGroupEntity cmpGroup;

    public final StringPath cmpId = createString("cmpId");

    public final StringPath groupId = createString("groupId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDt = _super.regDt;

    //inherited
    public final StringPath regId = _super.regId;

    public QCmpGroupMappingEntity(String variable) {
        this(CmpGroupMappingEntity.class, forVariable(variable), INITS);
    }

    public QCmpGroupMappingEntity(Path<? extends CmpGroupMappingEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCmpGroupMappingEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCmpGroupMappingEntity(PathMetadata metadata, PathInits inits) {
        this(CmpGroupMappingEntity.class, metadata, inits);
    }

    public QCmpGroupMappingEntity(Class<? extends CmpGroupMappingEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cmpEntity = inits.isInitialized("cmpEntity") ? new QCmpEntity(forProperty("cmpEntity")) : null;
        this.cmpGroup = inits.isInitialized("cmpGroup") ? new QCmpGroupEntity(forProperty("cmpGroup")) : null;
    }

}

