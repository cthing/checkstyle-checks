/*
 * Copyright 2013 C Thing Software
 * All rights reserved.
 */
package checkstyle;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cthing.leap.caracal.common.dao.AddressDao;
import com.cthing.leap.caracal.common.domain.Address;
import com.cthing.leap.caracal.common.domain.Country;
import com.cthing.leap.caracal.common.domain.State;
import com.cthing.leap.caracal.db.DBUtils;

import static com.cthing.leap.caracal.db.DBConstants.*;


/**
 * Implementation of the postal address data access object.
 */
@Repository
@Transactional(timeout = 100, readOnly = true)
public class GoodDaoImpl extends NamedParameterJdbcDaoSupport implements Dao {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAddressDaoImpl.class);

    private static final String FIND_SQL = String.format("SELECT * FROM %s WHERE %s=?",
            TBL_COMMON_ADDRESS,
            COMMON_ADDRESS.COL_ADDRESS__ID);

    private static final String DELETE_SQL = String.format("DELETE FROM %s WHERE %s=:id",
            TBL_COMMON_ADDRESS,
            COMMON_ADDRESS.COL_ADDRESS__ID);

    private static final String UPDATE_SQL = String.format("UPDATE %s SET %s=:typeId, %s=:addr1, %s=:addr2, %s=:city, %s=:stateCode, %s=:postalCode, %s=:countryCode WHERE %s=:id",
            TBL_COMMON_ADDRESS,
            COMMON_ADDRESS.COL_ADDRESS__TYPE_ID,
            COMMON_ADDRESS.COL_ADDRESS__ADDR1,
            COMMON_ADDRESS.COL_ADDRESS__ADDR2,
            COMMON_ADDRESS.COL_ADDRESS__CITY,
            COMMON_ADDRESS.COL_ADDRESS__STATE_CODE,
            COMMON_ADDRESS.COL_ADDRESS__POSTAL_CODE,
            COMMON_ADDRESS.COL_ADDRESS__COUNTRY_CODE,
            COMMON_ADDRESS.COL_ADDRESS__ID);

    private static final String INSERT_SQL = String.format("INSERT INTO %s SET %s=:typeId, %s=:addr1, %s=:addr2, %s=:city, %s=:stateCode, %s=:postalCode, %s=:countryCode",
            TBL_COMMON_ADDRESS,
            COMMON_ADDRESS.COL_ADDRESS__TYPE_ID,
            COMMON_ADDRESS.COL_ADDRESS__ADDR1,
            COMMON_ADDRESS.COL_ADDRESS__ADDR2,
            COMMON_ADDRESS.COL_ADDRESS__CITY,
            COMMON_ADDRESS.COL_ADDRESS__STATE_CODE,
            COMMON_ADDRESS.COL_ADDRESS__POSTAL_CODE,
            COMMON_ADDRESS.COL_ADDRESS__COUNTRY_CODE);

    /**
     * Maps a result set to an address object.
     */
    public static final class AddressMapper implements RowMapper<Address> {

        public AddressMapper() {
        }

        @Override
        public Address mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            final Address addr = new Address();
            addr.setId(rs.getLong(COMMON_ADDRESS.COL_ADDRESS__ID));
            addr.setAddr1(rs.getString(COMMON_ADDRESS.COL_ADDRESS__ADDR1));
            addr.setAddr2(rs.getString(COMMON_ADDRESS.COL_ADDRESS__ADDR2));
            addr.setCity(rs.getString(COMMON_ADDRESS.COL_ADDRESS__CITY));
            addr.setStateCode(State.safeValueOf(rs.getString(COMMON_ADDRESS.COL_ADDRESS__STATE_CODE)));
            addr.setPostalCode(rs.getString(COMMON_ADDRESS.COL_ADDRESS__POSTAL_CODE));
            addr.setCountryCode(Country.safeValueOf(rs.getString(COMMON_ADDRESS.COL_ADDRESS__COUNTRY_CODE)));
            addr.setTypeId(rs.getLong(COMMON_ADDRESS.COL_ADDRESS__TYPE_ID));
            return addr;
        }
    }


    public GoodDaoImpl(final DataSource dataSource) {
        setDataSource(dataSource);
    }

    /**
     * {@inheritDoc}
     * @see com.cthing.leap.caracal.common.dao.Dao#find(java.lang.Long)
     */
    @Override
    public Address find(final Long id) {
        LOG.trace("Finding address {}", id);
        return DBUtils.getOneOrNull(getJdbcTemplate().query(FIND_SQL, new AddressMapper(), id));
    }

    /**
     * {@inheritDoc}
     * @see com.cthing.leap.caracal.common.dao.Dao#insertOrUpdate(java.lang.Object)
     */
    @Override
    @Transactional(timeout = 100, readOnly = false)
    public Long insertOrUpdate(final Address address) {
        return (address.getId() == null) ? insert(address) : update(address);
    }

    private Long insert(final Address address) {
        LOG.trace("Inserting new address {}", address);
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(INSERT_SQL, createParameterSource(address), keyHolder);

        final Long id = keyHolder.getKey().longValue();
        address.setId(id);
        return id;
    }

    private Long update(final Address address) {
        LOG.trace("Updating address {}", address);
        getNamedParameterJdbcTemplate().update(UPDATE_SQL, createParameterSource(address));
        return address.getId();
    }

    /**
     * {@inheritDoc}
     * @see com.cthing.leap.caracal.common.dao.Dao#delete(java.lang.Object)
     */
    @Override
    @Transactional(readOnly = false)
    public void delete(final Address address) throws InvalidDataAccessApiUsageException {
        final Long id = address.getId();
        if (id == null) {
            throw new InvalidDataAccessApiUsageException("attempt to delete address with null ID");
        }

        LOG.trace("Deleting address {}", address);
        getNamedParameterJdbcTemplate().update(DELETE_SQL, createParameterSource(address));
        address.setId(null);
    }

    private SqlParameterSource createParameterSource(final Address address) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", address.getId());
        params.addValue("typeId", address.getTypeId());
        params.addValue("addr1", address.getAddr1());
        params.addValue("addr2", address.getAddr2());
        params.addValue("city", address.getCity());
        params.addValue("stateCode", (address.getStateCode() == null) ? null : address.getStateCode().toString());
        params.addValue("postalCode", address.getPostalCode());
        params.addValue("countryCode", (address.getCountryCode() == null) ? null : address.getCountryCode().toString());
        return params;
    }
}
