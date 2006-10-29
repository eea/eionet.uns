insert into UNS2_MIGRATION_TEST.CHANNEL_METADATA_ELEMENTS
select
c.ID as CHANNEL_ID,
cme.ID as METADATA_ELEMENT_ID,
0 as VISIBLE,
0 as APPEARANCE_ORDER,
0 as FILTERED,
NULL as OBSOLETE
from UNS2_MIGRATION_TEST.CHANNEL c,
(select me.ID, me.NAME from UNS2_MIGRATION_TEST.METADATA_ELEMENTS me where me.NAME in
(
select em.PROPERTY from UNS2_MIGRATION_TEST.EVENT e, UNS2_MIGRATION_TEST.EVENT_METADATA em where e.ID=em.EVENT_ID and e.CHANNEL_ID=22
union
select choo.PROPERTY from UNS1_PRODUCTION.choosable choo where choo.CHANNEL_ID=22
)
) as cme
where c.ID=22
