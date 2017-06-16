package mx.com.amx.unotv.workflow.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ExtraInfoContentDTO;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("workFlowDAO")
public class WorkFlowDAO {

	private JdbcTemplate jdbcTemplate;
	private final Logger log = Logger.getLogger(WorkFlowDAO.class);
	
	public  List<ContentDTO> getNotasMagazine ( String idMagazine, String idContenido ) 
	{
		StringBuffer sbQuery = new StringBuffer();
		try {
			
			sbQuery.append(" select ");  
			sbQuery.append(" N.FC_TITULO as fcTitulo, ");
			sbQuery.append(" N.FC_ID_CATEGORIA as fcIdCategoria, ");
			sbQuery.append(" CASE ");
			sbQuery.append(" WHEN TS.FC_ID_TIPO_SECCION = 'especiales' ");
			sbQuery.append(" THEN ('/'||COALESCE(TS.FC_ID_TIPO_SECCION,'')||'/'||COALESCE(S.FC_FRIENDLY_URL,'')||'/'||COALESCE(C.FC_FRIENDLY_URL,'')||'/detalle/'|| COALESCE(N.FC_NOMBRE,'')) "); 
			sbQuery.append(" ELSE ");
			sbQuery.append(" ('/'||COALESCE(TS.FC_ID_TIPO_SECCION,'')||'s/'||COALESCE(S.FC_FRIENDLY_URL,'')||'/'||COALESCE(C.FC_FRIENDLY_URL,'')||'/detalle/'|| COALESCE(N.FC_NOMBRE,'')) ");
			sbQuery.append(" END AS fcUrl, ");  
			sbQuery.append(" N.FC_IMAGEN_PRINCIPAL as fcImgPrincipal ");
			sbQuery.append(" from WPDB2INS.UNO_MX_N_NOTA as N, ");
			sbQuery.append(" WPDB2INS.UNO_MX_I_NOTA_MAGAZINE INM, ");
			sbQuery.append(" WPDB2INS.UNO_MX_C_CATEGORIA C, ");
			sbQuery.append(" WPDB2INS.UNO_MX_C_SECCION S, ");
			sbQuery.append(" WPDB2INS.UNO_MX_C_TIPO_SECCION TS ");
			sbQuery.append(" where INM.FC_ID_CONTENIDO=N.FC_ID_CONTENIDO ");
			sbQuery.append(" AND C.FC_ID_CATEGORIA=N.FC_ID_CATEGORIA ");
			sbQuery.append(" AND C.FC_ID_SECCION=S.FC_ID_SECCION AND S.FC_ID_TIPO_SECCION=TS.FC_ID_TIPO_SECCION "); 
			sbQuery.append(" AND INM.FC_ID_MAGAZINE= ? ");
			sbQuery.append(" AND INM.FC_ID_CONTENIDO!= ? ");
			sbQuery.append(" order by INM.FI_ORDEN ASC ");
			sbQuery.append(" FETCH FIRST 4 ROWS ONLY ");
			
		
			List< ContentDTO > list=jdbcTemplate.query ( sbQuery.toString() ,
					new Object [] { idMagazine, idContenido} , 
					new BeanPropertyRowMapper<ContentDTO>( ContentDTO.class) );
			return list;
		} catch (Exception e) {
			
			log.error(" Error getNotasMagazine [DAO] ",e );
			log.error("SQL: "+sbQuery);
			log.error("idMagazine: "+idMagazine);
		}
	
		return null;
	}
	
	public  String getIdNotaByName(String nameContenido ) 
	{
		String idContenido="";
		StringBuffer sbQuery = new StringBuffer();
		try {
			
			sbQuery.append(" SELECT N.FC_ID_CONTENIDO AS fcIdContenido ");
			sbQuery.append(" FROM WPDB2INS.UNO_MX_N_NOTA N ");
			sbQuery.append(" WHERE FC_NOMBRE= ? ");
			sbQuery.append(" FETCH FIRST 1 ROWS ONLY  ");

			List< ContentDTO > list=jdbcTemplate.query ( sbQuery.toString() ,
					new Object [] { nameContenido,} , 
					new BeanPropertyRowMapper<ContentDTO>( ContentDTO.class) );
			
			if(list!=null && list.size()>0)
				idContenido=list.get(0).getFcIdContenido();
				
		} catch (Exception e) {
			
			log.error(" Error getIdNotaByName [DAO] ",e );
			log.error("SQL: "+sbQuery);
			log.error("nameContenido: "+nameContenido);
		}
		
		return idContenido;
	}
	
	public  ExtraInfoContentDTO getExtraInfoContent(String friendlyURL ) 
	{
		ExtraInfoContentDTO respuesta=new ExtraInfoContentDTO();
		StringBuffer sbQuery = new StringBuffer();
		try {
			sbQuery.append("SELECT   ");
			sbQuery.append("CASE  ");
			sbQuery.append("WHEN TS.FC_ID_TIPO_SECCION ='especiales' THEN   ");
			sbQuery.append("('http://www.unotv.com/'||COALESCE(TS.FC_ID_TIPO_SECCION,'')||'/'||COALESCE(S.FC_FRIENDLY_URL,'')||'/'||COALESCE(C.FC_FRIENDLY_URL,'')||'/detalle/'|| COALESCE(N.FC_NOMBRE,'')) ||'/' ");
			sbQuery.append("ELSE "); 
			sbQuery.append("('http://www.unotv.com/'||COALESCE(TS.FC_ID_TIPO_SECCION,'')||'s/'||COALESCE(S.FC_FRIENDLY_URL,'')||'/'||COALESCE(C.FC_FRIENDLY_URL,'')||'/detalle/'|| COALESCE(N.FC_NOMBRE,'')) ||'/' ");
			sbQuery.append("END as url_nota,  ");
			sbQuery.append("C.FC_RUTA_DFP_APP as ruta_dfp,    ");
			sbQuery.append("C.FC_DESCRIPCION as desc_categoria,  ");
			sbQuery.append("S.FC_DESCRIPCION as desc_seccion ");
			sbQuery.append("FROM WPDB2INS.UNO_MX_H_NOTA N,     ");
			sbQuery.append("WPDB2INS.UNO_MX_C_TIPO_SECCION TS,  ");  
			sbQuery.append("WPDB2INS.UNO_MX_C_SECCION S,    ");
			sbQuery.append("WPDB2INS.UNO_MX_C_CATEGORIA  C ");   
			sbQuery.append("WHERE  ");
			sbQuery.append("C.FC_ID_CATEGORIA=N.FC_ID_CATEGORIA    ");
			sbQuery.append("AND C.FC_ID_SECCION=S.FC_ID_SECCION AND S.FC_ID_TIPO_SECCION=TS.FC_ID_TIPO_SECCION    ");
			sbQuery.append("AND N.FC_NOMBRE = ? ");
			
			List< ExtraInfoContentDTO > list=jdbcTemplate.query ( sbQuery.toString() ,
					new Object [] { friendlyURL,} , 
					new BeanPropertyRowMapper<ExtraInfoContentDTO>( ExtraInfoContentDTO.class) );
			
			if(list!=null && list.size()>0)
				respuesta=list.get(0);
				
		} catch (Exception e) {
			
			log.error(" Error getExtraInfoContent [DAO] ",e );
			log.error("SQL: "+sbQuery);
			log.error("friendlyURL: "+friendlyURL);
		}
		
		return respuesta;
	}
	
	public boolean deleteNotaTag(final String pstIdContenido){
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer sb = new StringBuffer();
		try {
							
			sb.append( " DELETE FROM WPDB2INS.UNO_APP_I_TAG_NOTA WHERE FC_ID_CONTENIDO = ?");	
		
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sb.toString());
					ps.setString( 1, pstIdContenido);
					return ps;
				}
			}				
			);				
			//if(numRegistrosActualizados > 0)
				success = true;			
		} catch (Exception e) {
			success = false;
			log.error("SQL: "+sb);
			log.error("Error en deleteNotaTag: ", e);				
		}
		return success;
	}	
	
	public boolean insertNotaTag(final String idContenido, final String idTag) {
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer query = new StringBuffer();
		try{
			
			query.append(" INSERT INTO WPDB2INS.UNO_APP_I_TAG_NOTA "); 
			query.append(" (FC_ID_CONTENIDO, FC_ID_TAG ) "); 
			query.append(" VALUES ( ");
			query.append(" ?, ? ");
			query.append(" ) ");
			
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query.toString());
					
					ps.setString( 1, idContenido);
					ps.setString( 2, idTag);				
					return ps;
				}
			}				
			);	
			
			if(numRegistrosActualizados > 0)
				success = true;
		}catch( Exception e ){
			log.error("SQL: "+query);
			log.error("Error en insertNotaTag: ",e);
		}
		return success;
	}
	
	public  List<ContentDTO> getRelacionadasbyIdCategoria( String idCategoria, String nameContenido ) 
	{
		StringBuffer sbQuery = new StringBuffer();
		try {
		
			sbQuery.append(" SELECT  ");
			sbQuery.append(" N.FC_NOMBRE AS fcNombre ");
			sbQuery.append(" ,N.FC_TITULO AS fcTitulo ");
			sbQuery.append(" ,N.FC_ID_TIPO_NOTA AS fcIdTipoNota ");
			sbQuery.append(" ,N.FC_ID_CATEGORIA AS fcIdCategoria ");
			sbQuery.append(" ,C.FC_DESCRIPCION AS fcNombreCategoria ");
			sbQuery.append(" ,N.FC_IMAGEN_PRINCIPAL AS fcImgPrincipal ");
			sbQuery.append(" ,N.FC_IMAGEN_PIE as fcPieFoto");
			sbQuery.append(" ,'/' || TS.FC_FRIENDLY_URL || '/' || S.FC_FRIENDLY_URL || '/' || C.FC_FRIENDLY_URL || '/detalle/' || N.FC_NOMBRE || '/' AS fcUrl ");
			sbQuery.append(" FROM WPDB2INS.UNO_MX_C_TIPO_SECCION TS ");
			sbQuery.append(" ,WPDB2INS.UNO_MX_C_SECCION S ");
			sbQuery.append(" ,WPDB2INS.UNO_MX_C_CATEGORIA C ");
			sbQuery.append(" ,WPDB2INS.UNO_MX_N_NOTA N ");
			sbQuery.append(" WHERE TS.FC_ID_TIPO_SECCION = S.FC_ID_TIPO_SECCION ");
			sbQuery.append(" AND S.FC_ID_SECCION = C.FC_ID_SECCION ");
			sbQuery.append(" AND C.FC_ID_CATEGORIA = N.FC_ID_CATEGORIA ");
			sbQuery.append(" AND N.FC_ID_CATEGORIA = ? ");
			sbQuery.append(" AND N.FC_NOMBRE != ? ");
			sbQuery.append(" AND TS.FI_ESTATUS = 1 ");
			sbQuery.append(" AND S.FI_ESTATUS = 1 ");
			sbQuery.append(" AND C.FI_ESTATUS = 1 ");
			sbQuery.append(" ORDER BY N.FD_FECHA_PUBLICACION DESC ");
			sbQuery.append(" FETCH FIRST 50 ROWS ONLY ");

			List< ContentDTO > list=jdbcTemplate.query ( sbQuery.toString() ,
					new Object [] { idCategoria, nameContenido,} , 
					new BeanPropertyRowMapper<ContentDTO>( ContentDTO.class) );
			return list;
		} catch (Exception e) {
			
			log.error(" Error getRelacionadasbyIdCategoria [DAO] ",e );
			log.error("SQL: "+sbQuery);
			log.error("idCategoria: "+idCategoria);
			log.error("nameContenido: "+nameContenido);
		}
		
		return null;
	}
	/*public Integer getSecuencia(){
		Integer secuencia = 0;
		try {
				final StringBuffer sb = new StringBuffer();				
				sb.append( " SELECT WPDB2INS.UNO_MX_SEQ_NOTA.NEXTVAL AS SECUENCIA FROM SYSIBM.SYSDUMMY1 ");	
			
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
			log.error("Error en getSecuencia: ", e);				
		}
		return secuencia;
	}*/
	
	public boolean existeNotaRegistrada(final String pstNameContenido){
		boolean success = false;
		try {
			String resultado = "false";
			final StringBuffer sb = new StringBuffer();				
			sb.append( " SELECT FC_NOMBRE FROM WPDB2INS.UNO_MX_N_NOTA WHERE FC_NOMBRE = ?");
		
			resultado = (String) jdbcTemplate.query(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					return connection.prepareStatement(sb.toString());
				}
			},
			new PreparedStatementSetter() {
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, pstNameContenido);
				}
			},
			new ResultSetExtractor<String>() {
				public String extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					int cont = 0;
					String result = "false";
					if( rs.next() ) 
				    	cont++;
				    	
				    if(cont > 0)
				    	result = "true";
				    return result;
				}
			});								
			
			if(resultado != null && resultado.trim().equals("true")) 
				success= true;
			
		} catch (Exception e) {
			success = false;
			log.error("Error en existeNotaRegistrada: ", e);
		}
		return success;
	}
	
	public boolean insertNotaBD(final ContentDTO contentDTO) {
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer query = new StringBuffer();
		try{
			
			query.append(" INSERT INTO WPDB2INS.UNO_MX_N_NOTA "); 
			query.append(" (FC_ID_CONTENIDO, FC_ID_CATEGORIA, FC_NOMBRE, FC_TITULO, "); //4
			query.append(" FC_DESCRIPCION, FC_ESCRIBIO, FC_LUGAR, FC_FUENTE, ");//8
			query.append(" FC_ID_TIPO_NOTA, FC_IMAGEN_PRINCIPAL, FC_IMAGEN_PIE, FC_VIDEO_YOUTUBE, ");//12
			query.append(" FC_ID_VIDEO_CONTENT, FC_ID_VIDEO_PLAYER, CL_GALERIA_IMAGENES, FC_INFOGRAFIA, ");//16
			query.append(" CL_RTF_CONTENIDO, FD_FECHA_PUBLICACION, FD_FECHA_MODIFICACION, FC_TAGS, ");//20
			query.append(" FC_KEYWORDS, FI_BAN_INFINITO_HOME, FI_BAN_VIDEO_VIRAL, FI_BAN_NO_TE_LO_PIERDAS, ");//24
			query.append(" FI_BAN_PATROCINIO, FC_PATROCINIO_BACKGROUND, FC_PATROCINIO_COLOR_TEXTO, FC_PLACE_GALLERY, FC_PCODE,  ");//29
			query.append(" FC_SOURCE_VIDEO, FC_ALTERNATE_TEXT, FC_DURATION, FC_FILE_SIZE )");//33
			query.append(" VALUES ( ");
			query.append(" ?, ?, ?, ?, "); //4
			query.append(" ?, ?, ?, ?, "); //8
			query.append(" ?, ?, ?, ?, "); //12
			query.append(" ?, ?, ?, ?, "); //16
			query.append(" ?, ?, CURRENT TIMESTAMP, ?, "); //19
			query.append(" ?, ?, ?, ?, "); //23
			query.append(" ?, ?, ?, ?, ?, ");//28
			query.append(" ?, ?, ?, ? "); //32
			query.append(" ) ");
			
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query.toString());
					
					ps.setString( 1, contentDTO.getFcIdContenido());
					ps.setString( 2, contentDTO.getFcIdCategoria());
					ps.setString( 3, contentDTO.getFcNombre());
					ps.setString( 4, contentDTO.getFcTitulo());
					
					ps.setString( 5, contentDTO.getFcDescripcion());
					ps.setString( 6, contentDTO.getFcEscribio());
					ps.setString( 7, contentDTO.getFcLugar());
					ps.setString( 8, contentDTO.getFcFuente());
					
					ps.setString(9, contentDTO.getFcIdTipoNota());
					ps.setString(10, contentDTO.getFcImgPrincipal());
					ps.setString(11, contentDTO.getFcPieFoto());
					ps.setString(12, contentDTO.getFcIdVideoYouTube());
					
					ps.setString(13, contentDTO.getFcIdVideoOoyala());
					ps.setString(14, contentDTO.getFcIdPlayerOoyala());
					ps.setString(15, contentDTO.getClGaleriaImagenes());
					ps.setString(16, contentDTO.getFcImgInfografia());
					
					ps.setString(17, contentDTO.getClRtfContenido());
					ps.setTimestamp(18, contentDTO.getFdFechaPublicacion());
					ps.setString(19, contentDTO.getFcTags());
					ps.setString(20, contentDTO.getFcKeywords());
					
					ps.setInt(21, contentDTO.getFiBanInfinito());
					ps.setInt(22, contentDTO.getFiBanVideoViral());
					ps.setInt(23, contentDTO.getFiBanNoTeLoPierdas());
					ps.setInt(24, contentDTO.getFiBanPatrocinio());
					
					ps.setString(25, contentDTO.getFcPatrocinioBackGround());
					ps.setString(26, contentDTO.getFcPatrocinioColorTexto());
					ps.setString(27, contentDTO.getPlaceGallery());
					ps.setString(28, contentDTO.getFcPCode());
					
					ps.setString(29, contentDTO.getFcSourceVideo());
					ps.setString(30, contentDTO.getFcAlternateTextVideo());
					ps.setString(31, contentDTO.getFcDurationVideo());
					ps.setString(32, contentDTO.getFcFileSizeVideo());
					
					return ps;
				}
			}				
			);	
			
			if(numRegistrosActualizados > 0)
				success = true;
		}catch( Exception e ){
			log.error("SQL: "+query);
			log.error("Error en insertNotaBD: ",e);
		}
		return success;
	}
	
	public boolean updateNotaBD(final ContentDTO contentDTO) {


		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer query = new StringBuffer();
		try{
			
			query.append(" UPDATE WPDB2INS.UNO_MX_N_NOTA "); 
			query.append(" SET FC_ID_CATEGORIA=?, FC_NOMBRE=?, FC_TITULO=?, FC_DESCRIPCION=?, "); //4
			query.append(" FC_ESCRIBIO=?, FC_LUGAR=?, FC_FUENTE=?, FC_ID_TIPO_NOTA=?, "); //8
			query.append(" FC_IMAGEN_PRINCIPAL=?, FC_IMAGEN_PIE=?, FC_VIDEO_YOUTUBE=?, FC_ID_VIDEO_CONTENT=?, ");//12
			query.append(" FC_ID_VIDEO_PLAYER=?, CL_GALERIA_IMAGENES=?, FC_INFOGRAFIA=?, CL_RTF_CONTENIDO=?, ");//16
			query.append(" FD_FECHA_PUBLICACION=?, FD_FECHA_MODIFICACION = CURRENT TIMESTAMP , FC_TAGS=?, FC_KEYWORDS=?, ");//19
			query.append(" FI_BAN_INFINITO_HOME=?, FI_BAN_VIDEO_VIRAL=?, FI_BAN_NO_TE_LO_PIERDAS=?, FI_BAN_PATROCINIO=?, ");//23
			query.append(" FC_PATROCINIO_BACKGROUND=?, FC_PATROCINIO_COLOR_TEXTO=?,  "); //25
			query.append(" FC_PLACE_GALLERY=?, FC_PCODE=?, "); //27
			query.append(" FC_SOURCE_VIDEO=?, FC_ALTERNATE_TEXT=?, FC_DURATION=?, FC_FILE_SIZE=? "); //31
			query.append(" WHERE FC_NOMBRE=? "); //32
			
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query.toString());
					ps.setString( 1, contentDTO.getFcIdCategoria());
					ps.setString( 2, contentDTO.getFcNombre());
					ps.setString( 3, contentDTO.getFcTitulo());
					ps.setString( 4, contentDTO.getFcDescripcion());
					
					ps.setString( 5, contentDTO.getFcEscribio());
					ps.setString( 6, contentDTO.getFcLugar());
					ps.setString( 7, contentDTO.getFcFuente());
					ps.setString( 8, contentDTO.getFcIdTipoNota());
					
					ps.setString(9, contentDTO.getFcImgPrincipal());
					ps.setString(10, contentDTO.getFcPieFoto());
					ps.setString(11, contentDTO.getFcIdVideoYouTube());
					ps.setString(12, contentDTO.getFcIdVideoOoyala());
					
					ps.setString(13, contentDTO.getFcIdPlayerOoyala());
					ps.setString(14, contentDTO.getClGaleriaImagenes());
					ps.setString(15, contentDTO.getFcImgInfografia());
					ps.setString(16, contentDTO.getClRtfContenido());
					
					ps.setTimestamp(17, contentDTO.getFdFechaPublicacion());
					ps.setString(18, contentDTO.getFcTags());
					ps.setString(19, contentDTO.getFcKeywords());
					ps.setInt(20, contentDTO.getFiBanInfinito());
					
					ps.setInt(21, contentDTO.getFiBanVideoViral());
					ps.setInt(22, contentDTO.getFiBanNoTeLoPierdas());
					ps.setInt(23, contentDTO.getFiBanPatrocinio());
					
					ps.setString(24, contentDTO.getFcPatrocinioBackGround());
					ps.setString(25, contentDTO.getFcPatrocinioColorTexto());
					ps.setString(26, contentDTO.getPlaceGallery());
					ps.setString(27, contentDTO.getFcPCode());
					
					ps.setString(28, contentDTO.getFcSourceVideo());
					ps.setString(29, contentDTO.getFcAlternateTextVideo());
					ps.setString(30, contentDTO.getFcDurationVideo());
					ps.setString(31, contentDTO.getFcFileSizeVideo());
					
					ps.setString(32, contentDTO.getFcNombre());
					return ps;
				}
			}				
			);	

			if(numRegistrosActualizados > 0)
				success = true;
		}catch( Exception e ){
			log.error("SQL: "+query);
			log.error("Error en updateNotaBD: ", e);
		}
		return success;
	}
	
	public boolean insertNotaHistoricoBD(final ContentDTO contentDTO) {
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer query = new StringBuffer();
		try{
			query.append(" INSERT INTO WPDB2INS.UNO_MX_H_NOTA "); 
			query.append(" (FC_ID_CONTENIDO, FC_ID_CATEGORIA, FC_NOMBRE, FC_TITULO, "); //4
			query.append(" FC_DESCRIPCION, FC_ESCRIBIO, FC_LUGAR, FC_FUENTE, ");//8
			query.append(" FC_ID_TIPO_NOTA, FC_IMAGEN_PRINCIPAL, FC_IMAGEN_PIE, FC_VIDEO_YOUTUBE, ");//12
			query.append(" FC_ID_VIDEO_CONTENT, FC_ID_VIDEO_PLAYER, CL_GALERIA_IMAGENES, FC_INFOGRAFIA, ");//16
			query.append(" CL_RTF_CONTENIDO, FD_FECHA_PUBLICACION, FD_FECHA_MODIFICACION, FC_TAGS, ");//20
			query.append(" FC_KEYWORDS, FI_BAN_INFINITO_HOME, FI_BAN_VIDEO_VIRAL, FI_BAN_NO_TE_LO_PIERDAS, ");//24
			query.append(" FI_BAN_PATROCINIO, FC_PATROCINIO_BACKGROUND, FC_PATROCINIO_COLOR_TEXTO, FC_PLACE_GALLERY, FC_PCODE,  ");//27
			query.append(" FC_SOURCE_VIDEO, FC_ALTERNATE_TEXT, FC_DURATION, FC_FILE_SIZE )");//33
			query.append(" VALUES ( ");
			query.append(" ?, ?, ?, ?, ");
			query.append(" ?, ?, ?, ?, ");
			query.append(" ?, ?, ?, ?, ");
			query.append(" ?, ?, ?, ?, ");
			query.append(" ?, ?, CURRENT TIMESTAMP, ?, ");
			query.append(" ?, ?, ?, ?, ");
			query.append(" ?, ?, ?, ?, ?, ");
			query.append(" ?, ?, ?, ? ");
			query.append(" ) ");
			
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query.toString());
					
					ps.setString( 1, contentDTO.getFcIdContenido());
					ps.setString( 2, contentDTO.getFcIdCategoria());
					ps.setString( 3, contentDTO.getFcNombre());
					ps.setString( 4, contentDTO.getFcTitulo());
					
					ps.setString( 5, contentDTO.getFcDescripcion());
					ps.setString( 6, contentDTO.getFcEscribio());
					ps.setString( 7, contentDTO.getFcLugar());
					ps.setString( 8, contentDTO.getFcFuente());
					
					ps.setString(9, contentDTO.getFcIdTipoNota());
					ps.setString(10, contentDTO.getFcImgPrincipal());
					ps.setString(11, contentDTO.getFcPieFoto());
					ps.setString(12, contentDTO.getFcIdVideoYouTube());
					
					ps.setString(13, contentDTO.getFcIdVideoOoyala());
					ps.setString(14, contentDTO.getFcIdPlayerOoyala());
					ps.setString(15, contentDTO.getClGaleriaImagenes());
					ps.setString(16, contentDTO.getFcImgInfografia());
					
					ps.setString(17, contentDTO.getClRtfContenido());
					ps.setTimestamp(18, contentDTO.getFdFechaPublicacion());
					ps.setString(19, contentDTO.getFcTags());
					ps.setString(20, contentDTO.getFcKeywords());
					
					ps.setInt(21, contentDTO.getFiBanInfinito());
					ps.setInt(22, contentDTO.getFiBanVideoViral());
					ps.setInt(23, contentDTO.getFiBanNoTeLoPierdas());
					ps.setInt(24, contentDTO.getFiBanPatrocinio());
					
					ps.setString(25, contentDTO.getFcPatrocinioBackGround());
					ps.setString(26, contentDTO.getFcPatrocinioColorTexto());
					ps.setString(27, contentDTO.getPlaceGallery());
					ps.setString(28, contentDTO.getFcPCode());
					
					ps.setString(29, contentDTO.getFcSourceVideo());
					ps.setString(30, contentDTO.getFcAlternateTextVideo());
					ps.setString(31, contentDTO.getFcDurationVideo());
					ps.setString(32, contentDTO.getFcFileSizeVideo());
					return ps;
				}
			}				
			);	
			
			if(numRegistrosActualizados > 0)
				success = true;
		}catch( Exception e ){
			log.error("SQL: "+query);
			log.error("Error en insertNotaHistoricoBD: ", e);		
		}
		return success;
	}
	
	public boolean updateNotaHistoricoBD(final ContentDTO contentDTO) {
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer query = new StringBuffer();
		try{			
			
			query.append(" UPDATE WPDB2INS.UNO_MX_H_NOTA "); 
			query.append(" SET FC_ID_CATEGORIA=?, FC_NOMBRE=?, FC_TITULO=?, FC_DESCRIPCION=?, "); //4
			query.append(" FC_ESCRIBIO=?, FC_LUGAR=?, FC_FUENTE=?, FC_ID_TIPO_NOTA=?, "); //8
			query.append(" FC_IMAGEN_PRINCIPAL=?, FC_IMAGEN_PIE=?, FC_VIDEO_YOUTUBE=?, FC_ID_VIDEO_CONTENT=?, ");//12
			query.append(" FC_ID_VIDEO_PLAYER=?, CL_GALERIA_IMAGENES=?, FC_INFOGRAFIA=?, CL_RTF_CONTENIDO=?, ");//16
			query.append(" FD_FECHA_PUBLICACION=?, FD_FECHA_MODIFICACION = CURRENT TIMESTAMP , FC_TAGS=?, FC_KEYWORDS=?, ");//20
			query.append(" FI_BAN_INFINITO_HOME=?, FI_BAN_VIDEO_VIRAL=?, FI_BAN_NO_TE_LO_PIERDAS=?, FI_BAN_PATROCINIO=?, ");//24
			query.append(" FC_PATROCINIO_BACKGROUND=?, FC_PATROCINIO_COLOR_TEXTO=?,  "); //26
			query.append(" FC_PLACE_GALLERY=?, FC_PCODE=?, ");
			query.append(" FC_SOURCE_VIDEO=?, FC_ALTERNATE_TEXT=?, FC_DURATION=?, FC_FILE_SIZE=? "); //32
			query.append(" WHERE FC_NOMBRE=? ");
			
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query.toString());
					ps.setString( 1, contentDTO.getFcIdCategoria());
					ps.setString( 2, contentDTO.getFcNombre());
					ps.setString( 3, contentDTO.getFcTitulo());
					ps.setString( 4, contentDTO.getFcDescripcion());
					
					ps.setString( 5, contentDTO.getFcEscribio());
					ps.setString( 6, contentDTO.getFcLugar());
					ps.setString( 7, contentDTO.getFcFuente());
					ps.setString( 8, contentDTO.getFcIdTipoNota());
					
					ps.setString(9, contentDTO.getFcImgPrincipal());
					ps.setString(10, contentDTO.getFcPieFoto());
					ps.setString(11, contentDTO.getFcIdVideoYouTube());
					ps.setString(12, contentDTO.getFcIdVideoOoyala());
					
					ps.setString(13, contentDTO.getFcIdPlayerOoyala());
					ps.setString(14, contentDTO.getClGaleriaImagenes());
					ps.setString(15, contentDTO.getFcImgInfografia());
					ps.setString(16, contentDTO.getClRtfContenido());
					
					ps.setTimestamp(17, contentDTO.getFdFechaPublicacion());
					ps.setString(18, contentDTO.getFcTags());
					ps.setString(19, contentDTO.getFcKeywords());
					ps.setInt(20, contentDTO.getFiBanInfinito());
					
					ps.setInt(21, contentDTO.getFiBanVideoViral());
					ps.setInt(22, contentDTO.getFiBanNoTeLoPierdas());
					ps.setInt(23, contentDTO.getFiBanPatrocinio());
					
					ps.setString(24, contentDTO.getFcPatrocinioBackGround());
					ps.setString(25, contentDTO.getFcPatrocinioColorTexto());
					ps.setString(26, contentDTO.getPlaceGallery());
					ps.setString(27, contentDTO.getFcPCode());
					
					ps.setString(28, contentDTO.getFcSourceVideo());
					ps.setString(29, contentDTO.getFcAlternateTextVideo());
					ps.setString(30, contentDTO.getFcDurationVideo());
					ps.setString(31, contentDTO.getFcFileSizeVideo());
					
					ps.setString(32, contentDTO.getFcNombre());
					return ps;
				}
			}				
			);
			
			if(numRegistrosActualizados > 0)
				success = true;

		}catch( Exception e ){
			log.error("SQL: "+query);
			log.error("Error en updateNotaHistoricoBD: ",e);
		}

		return success;
	}
	
	public boolean deleteNotaBD(final String pstIdContenido){
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer sb = new StringBuffer();
		try {
							
			sb.append( " DELETE FROM WPDB2INS.UNO_MX_N_NOTA WHERE FC_ID_CONTENIDO = ?");	
		
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sb.toString());
					ps.setString( 1, pstIdContenido);
					return ps;
				}
			}				
			);				
			if(numRegistrosActualizados > 0)
				success = true;			
		} catch (Exception e) {
			success = false;
			log.error("SQL: "+sb);
			log.error("Error en deleteNotaBD: ", e);				
		}
		return success;
	}		
	
	
	public boolean deleteNotaHistoricoBD(final String pstIdContenido){
		boolean success = false;
		int numRegistrosActualizados = 0;
		final StringBuffer sb = new StringBuffer();
		try {
			
			sb.append( " DELETE FROM WPDB2INS.UNO_MX_H_NOTA WHERE FC_ID_CONTENIDO = ?");	
		
			numRegistrosActualizados = jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps = connection.prepareStatement(sb.toString());
					ps.setString( 1, pstIdContenido);
					return ps;
				}
			}				
			);				
			if(numRegistrosActualizados > 0)
				success = true;			
		} catch (Exception e) {
			success = false;
			log.error("SQL: "+sb);
			log.error("Error en deleteNotaHistoricoBD: ", e);				
		}
		return success;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
}
