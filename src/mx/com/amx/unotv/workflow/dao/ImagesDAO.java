package mx.com.amx.unotv.workflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
@Qualifier("imagesDAO")
public class ImagesDAO {
	
	private Logger logger=Logger.getLogger(ImagesDAO.class);
	
	public JdbcTemplate jdbcTemplate;

	public Integer getSequenceImage(){
		Integer secuencia = 0;
		try {
			final StringBuffer sb = new StringBuffer();				
			sb.append( " SELECT WPDB2INS.UNO_MX_SEQ_IMAGENES.NEXTVAL AS SECUENCIA FROM SYSIBM.SYSDUMMY1 ");	
		
			secuencia = (Integer) jdbcTemplate.query(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					return connection.prepareStatement(sb.toString());
				}
			},
			new ResultSetExtractor<Integer>() {
				public Integer extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					int sec = 0;
					if( rs.next() ) 
				    	sec = rs.getInt("SECUENCIA");				    					   
				    return sec;
				}
			});								
			
			
			
		} catch (Exception e) {
			secuencia = 0;
			logger.error("Error getSequenceImage [DAO]: ", e);				
		}
		return secuencia;
	}
	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
