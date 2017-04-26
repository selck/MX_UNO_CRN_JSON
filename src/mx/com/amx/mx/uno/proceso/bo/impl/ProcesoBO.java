package mx.com.amx.mx.uno.proceso.bo.impl;

import java.util.Properties;

import mx.com.amx.mx.uno.proceso.bo.IProcesoBO;
import mx.com.amx.mx.uno.proceso.dto.Categorias;
import mx.com.amx.mx.uno.proceso.dto.Noticias;
import mx.com.amx.mx.uno.proceso.dto.ParametrosDTO;
import mx.com.amx.mx.uno.proceso.dto.Secciones;
import mx.com.amx.mx.uno.proceso.utils.JSON;
import mx.com.amx.mx.uno.proceso.utils.ObtenerProperties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ProcesoBO implements IProcesoBO {
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	@Value( "${ambiente}" )
	String ambiente = "";	
	
	private  String URL_DAO = "";
	private final Properties props = new Properties();
	
	private RestTemplate restTemplate;
	HttpHeaders headers = new HttpHeaders();
	
	public ProcesoBO() {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
		
		if ( factory instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) factory).setConnectTimeout( 35 * 1000 );
			((SimpleClientHttpRequestFactory) factory).setReadTimeout( 35 * 1000 );
		} else if ( factory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( 35 * 1000);
			((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( 35 * 1000);
		}
		
		restTemplate.setRequestFactory( factory );
		headers.setContentType(MediaType.APPLICATION_JSON);
	      
		try {
			props.load( this.getClass().getResourceAsStream( "/general.properties" ) );						
		} catch(Exception e) {
			LOG.error("[ConsumeWS::init]Error al iniciar y cargar arhivo de propiedades." + e.getMessage());
		}
		ambiente = props.getProperty("ambiente");
		URL_DAO = props.getProperty(ambiente+".ws.url.api.servicios");
		
	}
	
	public void procesoAutomatico() {
		JSON getInfo = new JSON();
		LOG.info("************INI: GENERAR JSON *************");
		getInfo.writeNewsML();
		LOG.info("************FIN: GENERAR JSON *************");
		LOG.info(".::::: INI: GENERAR JSON TWITTER :::::.");
		getInfo.generaTwitterApp();
		LOG.info(".::::: FIN: GENERAR JSON TWITTER :::::.");
		LOG.info(".::::: INI: GENERAR JSON MAGAZINE :::::.");
		getInfo.generaJSONMagazine();
		LOG.info(".::::: FIN: GENERAR JSON MAGAZINE :::::.");
		LOG.info(".::::: INI: GENERAR JSON CARRUSEL CLAROSPORTS :::::.");
		getInfo.generaJSONCarruselClarosports();
		LOG.info(".::::: FIN: GENERAR JSON CARRUSEL CLAROSPORTS :::::.");
		LOG.info(".::::: INI: GENERAR JSON AMP :::::.");
		getInfo.generaJSONAMP();
		LOG.info(".::::: FIN: GENERAR JSON AMP :::::.");
		if(ambiente!=null && ambiente.equalsIgnoreCase("desarrollo")){
			ObtenerProperties pro=new ObtenerProperties();
			ParametrosDTO parametros= pro.obtenerPropiedades();
			getInfo.transfiereWebServer(parametros.getPathShell(), parametros.getRutaCarpeta(), parametros.getRutaDestino());
			getInfo.transfiereWebServer(parametros.getPathShell(), "/var/dev-repos/unotv/amp/json/", "/var/www/unotv/utils/amp/json/news.json");
		}
	}
	/**
	 * Metodo que obtiene las noticias según el id de Magazine.
	 * @param String idMagazine
	 * @throws Exception
	 * @author Jesus
	 * */
	public Noticias consultarNoticiasMagazineJSON(String idMagazine) {
		Noticias noticias = new Noticias();
		try{
			String lstMETODO = "/consultarNoticiasMagazineJSON";
			String lstURL_WS = URL_DAO + lstMETODO;
			HttpEntity<String> entity = new HttpEntity<String>( idMagazine, headers);
			noticias = restTemplate.postForObject(lstURL_WS , entity, Noticias.class);

		}catch(Exception e){
			LOG.error("Error consultarNoticiasMagazineJSON [BO] :",e);
			noticias.setNoticiasLst(null);
		}
		return noticias;	
	}
	/**
	 * Metodo que obtiene las noticias según el id de Magazine.
	 * @param String idMagazine
	 * @throws Exception
	 * @author Jesus
	 * */
	public Noticias consultarCarruselClarosportsJSON(int numRegistros) {
		Noticias noticias = new Noticias();
		try{
			String lstMETODO = "/consultarCarruselClarosportsJSON";
			String lstURL_WS = URL_DAO + lstMETODO;
			HttpEntity<Integer> entity = new HttpEntity<Integer>( numRegistros, headers);
			noticias = restTemplate.postForObject(lstURL_WS , entity, Noticias.class);

		}catch(Exception e){
			LOG.error("Error consultarCarruselClarosportsJSON [BO] :",e);
			noticias.setNoticiasLst(null);
		}
		return noticias;	
	}
	/**
	 * Metodo que obtiene las noticias según la categoria.
	 * @param String idCategoria
	 * */
	public Noticias consultarNoticias( String idCategoria ) throws Exception {
		Noticias noticias = new Noticias();
		try{
			String lstMETODO = "/consultarNoticias";
			String lstURL_WS = URL_DAO + lstMETODO;
			
			HttpEntity<String> entity = new HttpEntity<String>( idCategoria, headers);
			noticias = restTemplate.postForObject(lstURL_WS , entity, Noticias.class);

		}catch(Exception e){
			LOG.error("Error consultarNoticias [BO] :",e);
			noticias.setNoticiasLst(null);
		}
		return noticias;		
	}
	
	/**
	 * Metodo que obtiene las noticias según la sección.
	 * @param String idSeccion
	 * */
	public Noticias consultarUltimasPorSeccion( String idSeccion ) throws Exception {
		Noticias noticias = new Noticias();
		try{
			String lstMETODO = "/consultarUltimasPorSeccion";
			String lstURL_WS = URL_DAO + lstMETODO;
			
			HttpEntity<String> entity = new HttpEntity<String>( idSeccion, headers);
			noticias = restTemplate.postForObject(lstURL_WS , entity, Noticias.class);
						
		}catch(Exception e){
			LOG.error("Error consultarUltimasPorSeccion [BO] :",e);
			noticias.setNoticiasLst(null);
		}
		return noticias;		
	}
	
	/**
	 * Metodo que obtiene las noticias según el tipo de sección.
	 * @param String idSeccion
	 * */
	public Noticias consultarUltimasPorTipoSeccion( String idSeccion ) throws Exception {
		
		Noticias noticias = new Noticias();
		try{
			String lstMETODO = "/consultarUltimasPorTipoSeccion";
			String lstURL_WS = URL_DAO + lstMETODO;
			
			HttpEntity<String> entity = new HttpEntity<String>( idSeccion, headers);
			noticias = restTemplate.postForObject(lstURL_WS , entity, Noticias.class);
						
		}catch(Exception e){
			LOG.error("Error consultarUltimasPorTipoSeccion [BO] :",e);
			noticias.setNoticiasLst(null);
		}
		return noticias;		
	}
	
	/**
	 * Metodo que obtiene las categorias.
	 * */
	public Categorias getCategorias( ) throws Exception {
		
		Categorias categorias = new Categorias();
		try{
			String lstMETODO = "/getCategorias";
			String lstURL_WS = URL_DAO + lstMETODO;
			
			HttpEntity<Integer> entity = new HttpEntity<Integer>( headers);
			categorias = restTemplate.postForObject(lstURL_WS , entity, Categorias.class);
						
		}catch(Exception e){
			LOG.error("Error getCategorias [BO] :",e);
			categorias.setCategotiasLst(null);
		}
		return categorias;		
	}
	
	/**
	 * Metodo que obtiene las secciones.
	 * */
	public Secciones getSecciones( ) throws Exception {
		
		Secciones secciones = new Secciones();
		try{
			String lstMETODO = "/getSecciones";
			String lstURL_WS = URL_DAO + lstMETODO;
			
			HttpEntity<Integer> entity = new HttpEntity<Integer>( headers);
			secciones = restTemplate.postForObject(lstURL_WS , entity, Secciones.class);
						
		}catch(Exception e){
			LOG.error("Error getSecciones [BO] :",e);
			secciones.setSeccionesLst(null);
		}
		return secciones;		
	}
	
	/**
	 * Metodo que obtiene los tipos secciones.
	 * */
	public Secciones getTipoSecciones( ) throws Exception {
		
		Secciones secciones = new Secciones();
		try{
			String lstMETODO = "/getTipoSecciones";
			String lstURL_WS = URL_DAO + lstMETODO;
			
			HttpEntity<Integer> entity = new HttpEntity<Integer>( headers);
			secciones = restTemplate.postForObject(lstURL_WS , entity, Secciones.class);
						
		}catch(Exception e){
			LOG.error("Error getTipoSecciones [BO] :",e);
			secciones.setSeccionesLst(null);
		}
		return secciones;		
	}
	
}
