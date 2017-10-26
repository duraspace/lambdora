package org.fcrepo.lambdora.service.api;

/**
 * Constants class containing frequently used Fedora type strings.
 *
 * @author dbernstein
 */
public class FedoraTypes {

    private FedoraTypes() {
    }

    public static final String INTERNAL_PATH_PREFIX = "fedora-info:/";

    public static final String FEDORA_BINARY = "fedora:Binary";

    public static final String FEDORA_CONTAINER = "fedora:Container";

    public static final String FEDORA_NON_RDF_SOURCE_DESCRIPTION = "fedora:NonRdfSourceDescription";

    public static final String FEDORA_REPOSITORY_ROOT = "fedora:RepositoryRoot";

    public static final String FEDORA_RESOURCE = "fedora:Resource";

    public static final String FEDORA_SKOLEM = "fedora:Skolem";

    public static final String FEDORA_TOMBSTONE = "fedora:Tombstone";

    public static final String LDP_BASIC_CONTAINER = "ldp:BasicContainer";

    public static final String LDP_CONTAINER = "ldp:Container";

    public static final String LDP_DIRECT_CONTAINER = "ldp:DirectContainer";

    public static final String LDP_INDIRECT_CONTAINER = "ldp:IndirectContainer";

    public static final String LDP_INSERTED_CONTENT_RELATION = "ldp:insertedContentRelation";

    public static final String LDP_NON_RDF_SOURCE = "ldp:NonRDFSource";

    public static final String LDP_RDF_SOURCE = "ldp:RDFSource";

    public static final String FEDORA_LASTMODIFIED = "fedora:lastModified";

    public static final String FEDORA_CREATED = "fedora:created";

    public static final String FEDORA_LASTMODIFIEDBY = "fedora:lastModifiedBy";

    public static final String FEDORA_CREATEDBY = "fedora:createdBy";

    public static final String FILENAME = "ebucore:filename";

    public static final String HAS_MIME_TYPE = "ebucore:hasMimeType";

    public static final String CONTENT_SIZE = "premis:hasSize";

    public static final String CONTENT_DIGEST = "premis:hasMessageDigest";

    public static final String DEFAULT_DIGEST_ALGORITHM = "fedoraconfig:defaultDigestAlgorithm";

    public static final String FCR_METADATA = "fcr:metadata";

    public static final String FCR_VERSIONS = "fcr:versions";

    public static final String LDP_HAS_MEMBER_RELATION = "ldp:hasMemberRelation";

    public static final String LDP_IS_MEMBER_OF_RELATION = "ldp:isMemberOfRelation";

    public static final String LDP_MEMBER_RESOURCE = "ldp:membershipResource";

}
