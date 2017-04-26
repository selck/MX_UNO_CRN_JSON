package mx.com.amx.mx.uno.proceso.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.com.amx.mx.uno.proceso.bo.impl.ProcesoBO;
import mx.com.amx.mx.uno.proceso.dto.CategoriaDTO;
import mx.com.amx.mx.uno.proceso.dto.NoticiaRSSDTO;
import mx.com.amx.mx.uno.proceso.dto.ParametrosDTO;
import mx.com.amx.mx.uno.proceso.dto.SeccionDTO;
import mx.com.amx.mx.uno.proceso.dto.TipoSeccionDTO;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class JSON {
	
	private static final Logger log = Logger.getLogger(JSON.class);
	
	private List<NoticiaRSSDTO> lstNoticias;
	private ParametrosDTO parametros;
	private ProcesoBO procesoBO;
	
	public JSON(){
		ObtenerProperties props = new ObtenerProperties();
		parametros = props.obtenerPropiedades();
	}
	 
	public static String eliminaEspacios(String cad) {
		String cadena = "";
		cad = cad.trim();
		cadena = cad.replaceAll("\\s+", " ");
		cadena = cad.replaceAll("\t", "");
		cadena = cad.replaceAll("\t", "");
		cadena = cad.replaceAll("\n", "");
		cadena = cad.replace("^\\s+", "");
		cadena = cad.replace("\\s+$", "");
		return cadena.trim();
	}
	
	public static String filter(String str) {
		StringBuilder filtered = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);
			if (current == '�') {
				filtered.append("");
			} else{
				filtered.append(current);
			}
		}
		return filtered.toString();
	}

	private static String limpiaHTML(String HTML) {
		HTML = HTML.replace("<p dir=\"ltr\">", "");
		HTML = HTML.replace("<p dir=\"ltr\" style=\"text-align: justify;\">", "");
		HTML = HTML.replace("&nbsp;</p>", "");
		HTML = HTML.replace("</p>", "");
		HTML = HTML.replace("<div dir=\"ltr\">", "");		
		HTML = HTML.replace("&nbsp;</div>", "");
		HTML = HTML.replace("</div>", "");
		HTML = HTML.replace("<strong>", "");		
		HTML = HTML.replace("</strong>", "");		
		HTML = HTML.replace("&nbsp;", "");
		HTML = HTML.replace("<span style=\"font-size: 12px;\">", "");
		HTML = HTML.replace("</span>", "");
		HTML = HTML.replace("<br />", "");
		HTML = HTML.replace("<br/>", "");
		
		return HTML;		
	}
	public void generaJSONAMP() {
		try {
			boolean success = false;
			
			lstNoticias = consultarNoticiasMagazineJSON(parametros.getIdMagazineHome());
			if (lstNoticias != null && lstNoticias.size() > 0) {
				JSONObject respuestaJSON = new JSONObject();
				JSONArray arregloNotas = new JSONArray();
				
				try {
					for (int temp = 0; temp < 4; temp++) {
						NoticiaRSSDTO nota = (NoticiaRSSDTO) lstNoticias.get(temp);
						
						/*String rtfContenido = nota.getCL_RTF_CONTENIDO();
						rtfContenido = CodificaCaracteres.deleteBloque(rtfContenido, "<", ">");
						rtfContenido = CodificaCaracteres.deleteBloque(rtfContenido, "[", "]");*/
						
						JSONObject notaJSON = new JSONObject();
						//notaJSON.put("id", CodificaCaracteres.deleteHtmlTag(nota.getFC_ID_CONTENIDO()));
						notaJSON.put("title", CodificaCaracteres.deleteHtmlTag(nota.getFC_TITULO()));
						notaJSON.put("tag", CodificaCaracteres.deleteHtmlTag(nota.getFC_ID_CATEGORIA()));
						notaJSON.put("thumbnail", CodificaCaracteres.deleteHtmlTag(parametros.getDominio()+nota.getFC_IMAGEN_PRINCIPAL()));
						notaJSON.put("url", CodificaCaracteres.deleteHtmlTag(nota.getFcLink()));
						arregloNotas.put(notaJSON);
					}
					respuestaJSON.put("items", arregloNotas);
					success = true;
				} catch (Exception e) {
					log.error("Error en generaJSONAMP: ", e);
					success = false;
				}
				if (success) {
					Writer wt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parametros.getRutaCarpetaJsonAMP() + parametros.getNombreJsonAMP()), "UTF-8"));
					//Writer wt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/var/dev-repos/unotv/amp/json/news.json"), "UTF-8"));
					try {
						wt.write(respuestaJSON.toString());
					} finally {
						wt.close();
					}
				}
			}
		} catch (Exception e) {
			log.error("Error en generaJSONAMP: ", e);
		}

	}
	private void generaJSON(String stNombreArchivo, String stNombreSeccion, String tipoSeccion) {

		try {
			if(tipoSeccion.equalsIgnoreCase("seccion")){
				lstNoticias = consultarUltimasPorSeccion(stNombreSeccion);
			} else if(tipoSeccion.equalsIgnoreCase("tipoSeccion")){
				lstNoticias = consultarUltimasPorTipoSeccion(stNombreSeccion);
			} else{
				lstNoticias = obtenerNoticias(stNombreSeccion);
			}

			
			String [] limSec = parametros.getLimitarSeccion().trim().equals("")? null: parametros.getLimitarSeccion().split("\\|");			
			
			if (lstNoticias != null && lstNoticias.size() > 0) {

				ArrayList<String> creadas = new ArrayList<String>();
				JSONObject noticias = new JSONObject();
				JSONArray arregloNoticias = new JSONArray();
				JSONObject respuestaJSON = new JSONObject();
				JSONArray arregloNoticiasAPP = new JSONArray();
				int hh = 0;
				
				for (int temp = 0; temp < lstNoticias.size(); temp++) {
					NoticiaRSSDTO dto = (NoticiaRSSDTO) lstNoticias.get(temp);
					try {
						String imgPrincipal = dto.getFC_IMAGEN_PRINCIPAL() == null ? "" : dto.getFC_IMAGEN_PRINCIPAL().trim();

						for (String imagenAPPReplace : parametros.getImagenesAPPReplace().split("\\|")) {
							String[] imagenAppReplacer = imagenAPPReplace.split("=");

							imgPrincipal = imgPrincipal.replace(imagenAppReplacer[0], imagenAppReplacer[1]);
						}
						//String[] seccion = dto.getFcLink().split("\\/");

						JSONObject noticia = new JSONObject();
						noticia.put("id_noticia", dto.getFC_ID_CONTENIDO());
						noticia.put("id_seccion", dto.getFI_ID_APPS_TIPOSEC()+"");
						noticia.put("id_subseccion", dto.getFI_ID_APPS_SEC()+"");
						noticia.put("id_categoria", dto.getFI_ID_APPS_CAT()+"");
						
						noticia.put("titulo", CodificaCaracteres.cambiaCaracteres(dto.getFC_TITULO()));
						noticia.put("descripcion", CodificaCaracteres.cambiaCaracteres(dto.getFC_DESCRIPCION()));
						noticia.put("detalle", limpiaHTML(dto.getCL_RTF_CONTENIDO()));
						
						noticia.put("fecha", dto.getFD_FECHA_PUBLICACION());//fc.format(dto.getFD_FECHA_PUBLICACION()));
						noticia.put("poster_s", parametros.getDominio() + imgPrincipal);
						noticia.put("poster_m", parametros.getDominio() + imgPrincipal);
						noticia.put("poster_b", parametros.getDominio() + imgPrincipal);
						
						String tipo = dto.getFC_ID_TIPO_NOTA() == null ? "" : dto.getFC_ID_TIPO_NOTA();
						//log.info("tipo noticia: "+tipo);
						
						if (tipo.trim().equalsIgnoreCase("video")) {
							String urlVideoYouTube = dto.getFC_VIDEO_YOUTUBE() == null ? "" : dto.getFC_VIDEO_YOUTUBE();
							if (urlVideoYouTube.trim().equals("")) {
								urlVideoYouTube = dto.getFC_ID_VIDEO_CONTENT() == null ? "" : dto.getFC_ID_VIDEO_CONTENT();
							}
							if (!urlVideoYouTube.trim().equals("")) {
								noticia.put("video_url", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
								noticia.put("video_url_ios", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
								noticia.put("video_url_android", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
								noticia.put("video_url_bb", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
							}
						}
						noticia.put("lugar", CodificaCaracteres.cambiaCaracteres(dto.getFC_LUGAR()));
						noticia.put("fuente", CodificaCaracteres.cambiaCaracteres(dto.getFC_FUENTE()));
						noticia.put("fuente_img", CodificaCaracteres.cambiaCaracteres(dto.getFC_IMAGEN_PIE()));
						
						String link = parametros.getDominio() + "/" + dto.getFcLink() + "/" ;
						noticia.put("link", link);
						noticia.put("lugar", CodificaCaracteres.cambiaCaracteres(dto.getFC_LUGAR()));
						arregloNoticias.put(noticia);
						int totalLimite = 0;
						String tmSe = "";

						for (int ali = 0; ali < limSec.length; ali++) {
							String tmp[] = limSec[ali].split(",");
							if (tmp[2].trim().equals(stNombreSeccion)) {
								if (link.contains(tmp[0])) {
									tmSe = tmp[0];
									totalLimite = Integer.parseInt(tmp[1]);
									creadas.add(tmp[0]);
								}
							}
						}

						int contLi = 0;
						if (totalLimite > 0) {
							for (int cc = 0; cc < creadas.size(); cc++) {
								if (creadas.get(cc).trim().equals(tmSe))
									contLi++;
							}

						}
						if (contLi <= totalLimite) {
							if (hh < 15) {
								JSONObject noticiaAPP = new JSONObject();
								noticiaAPP.put("id_noticia", dto.getFC_ID_CONTENIDO());
								noticiaAPP.put("id_seccion", dto.getFI_ID_APPS_TIPOSEC()+"");
								noticiaAPP.put("id_subseccion", dto.getFI_ID_APPS_SEC()+"");
								noticiaAPP.put("id_categoria", dto.getFI_ID_APPS_CAT()+"");
								noticiaAPP.put("titulo", dto.getFC_TITULO());
								noticiaAPP.put("descripcion", dto.getFC_DESCRIPCION());
								noticiaAPP.put("detalle", limpiaHTML(dto.getCL_RTF_CONTENIDO()));
								noticiaAPP.put("fecha", dto.getFD_FECHA_PUBLICACION());//fc.format(dto.getFD_FECHA_PUBLICACION()));
								noticiaAPP.put("poster_s", parametros.getDominio() + imgPrincipal);
								noticiaAPP.put("poster_m", parametros.getDominio() + imgPrincipal);
								noticiaAPP.put("poster_b", parametros.getDominio() + imgPrincipal);
								if (tipo.trim().equalsIgnoreCase("video")) {
									String urlVideoYouTube = dto.getFC_VIDEO_YOUTUBE() == null ? "" : dto.getFC_VIDEO_YOUTUBE();
									if (urlVideoYouTube.trim().equals("")) {
										urlVideoYouTube = dto.getFC_ID_VIDEO_CONTENT() == null ? "" : dto.getFC_ID_VIDEO_CONTENT();
									}
									if (!urlVideoYouTube.trim().equals("")) {
										noticiaAPP.put("video_url", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
										noticiaAPP.put("video_url_ios", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
										noticiaAPP.put("video_url_android", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
										noticiaAPP.put("video_url_bb", parametros.getVideoURL() + urlVideoYouTube + parametros.getVideoURLFin());
									}
								}
								noticiaAPP.put("link", link);
								noticiaAPP.put("lugar", dto.getFC_LUGAR());
								arregloNoticiasAPP.put(noticiaAPP);
							}
							hh++;
						}
						 
					} catch (Exception e) {
						log.error("Error :", e);
					}
				}

				try {
					noticias.put("noticias", arregloNoticias);
					FileWriter fw = new FileWriter(parametros.getRutaCarpeta() + stNombreArchivo);
					fw.write(noticias.toString());
					fw.flush();
					fw.close();

					respuestaJSON.put("mensaje_respuesta", "OK");
					respuestaJSON.put("codigo_respuesta", "0");
					respuestaJSON.put("noticias", arregloNoticiasAPP);
					FileWriter fwr = new FileWriter(parametros.getRutaCarpeta() + "noticias" + stNombreArchivo);
					fwr.write(respuestaJSON.toString());
					fwr.flush();
					fwr.close();

					respuestaJSON = new JSONObject();
					respuestaJSON.put("mensaje_respuesta", "OK");
					respuestaJSON.put("codigo_respuesta", "0");
					respuestaJSON.put("noticias", arregloNoticiasAPP);

					fwr = new FileWriter(parametros.getRutaCarpeta() + "appnoticias" + stNombreArchivo);
					fwr.write(respuestaJSON.toString());
					fwr.flush();
					fwr.close();

					log.info("Se creo correctamente el archivo: " + stNombreArchivo);
				} catch (Exception e) {
					log.error("Error al escribir los archivos: ", e);
				}

//				transfiereWebServer(parametros.getPathShell(), parametros.getRutaCarpeta() + "*", parametros.getRutaDestino());
			}

		} catch (Exception e) {
			// e.printStackTrace();
			log.info("Error " + e);
		}
	}
	
	public static void main(String[] args) {
	    try {
	    	Date date=new Date();
	    	Timestamp timestamp = new Timestamp(date.getTime());//instead of date put your converted date
	    	Timestamp myTimeStamp= timestamp;
	    	/*String fecha="Sun May 29 19:00:00 CDT 2016";
	        SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
	        Date date = parserSDF.parse(fecha);*/
	        System.out.println("date: " + date.toString());
	        System.out.println(timestamp.toString());
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
	public void generaJSONMagazine() {
		try {
			boolean success = false;
			lstNoticias = consultarNoticiasMagazineJSON(parametros.getIdMagazineHome());
			if (lstNoticias != null && !lstNoticias.isEmpty()) {
				//log.debug("generaJSONMagazine notas no vacias");
				JSONObject respuestaJSON = new JSONObject();
				JSONArray arregloNotas = new JSONArray();
				
				try {
					for (int temp = 0; temp < lstNoticias.size(); temp++) {
						NoticiaRSSDTO nota = (NoticiaRSSDTO) lstNoticias.get(temp);
						
						String rtfContenido = nota.getCL_RTF_CONTENIDO();
						rtfContenido = CodificaCaracteres.deleteBloque(rtfContenido, "<", ">");
						rtfContenido = CodificaCaracteres.deleteBloque(rtfContenido, "[", "]");
						
						JSONObject notaJSON = new JSONObject();
						notaJSON.put("id", CodificaCaracteres.deleteHtmlTag(nota.getFC_ID_CONTENIDO()));
						notaJSON.put("titulo", CodificaCaracteres.deleteHtmlTag(nota.getFC_TITULO()));
						//notaJSON.put("descripcion", RedSocialEmbedding.getEmbedPost(nota.getCL_RTF_CONTENIDO()));
						notaJSON.put("descripcion", CodificaCaracteres.deleteHtmlTag(rtfContenido));
						notaJSON.put("url_imagen", CodificaCaracteres.deleteHtmlTag(parametros.getDominio()+nota.getFC_IMAGEN_PRINCIPAL()));
						notaJSON.put("url_contenido", CodificaCaracteres.deleteHtmlTag(nota.getFcLink()+"?utm_source=carrusel&utm_medium=link&utm_campaign=claroideas"));
						arregloNotas.put(notaJSON);
					}
					//Timestamp fechaTimeStamp = new Timestamp(lstNoticias.get(0).getFD_FECHA_MODIFICACION().getTime());
					String fecha = lstNoticias.get(0).getFD_FECHA_PUBLICACION();
					//log.debug("fecha:" + fecha);
					respuestaJSON.put("mensaje", "exito");
					respuestaJSON.put("codigo", "0");
					respuestaJSON.put("ultima_actualizacion", fecha);
					respuestaJSON.put("proveedor", "UnoTV");
					respuestaJSON.put("carrusel", arregloNotas);
					success = true;
				} catch (Exception e) {
					log.error("Error: ", e);
					success = false;
				}
				if (success) {
					/*
					FileWriter fw = new FileWriter(parametros.getRutaCarpetaJsonMagazine() + parametros.getNombreJsonMagazine());
					fw.write(respuestaJSON.toString());
					fw.flush();
					fw.close();
					*/
					Writer wt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parametros.getRutaCarpetaJsonMagazine() + parametros.getNombreJsonMagazine()), "UTF-8"));
					try {
						wt.write(respuestaJSON.toString());
					} finally {
						wt.close();
					}
				}
//				transfiereWebServer(parametros.getPathShell(), parametros.getRutaCarpeta() + "twitter_app.json", parametros.getRutaDestino());
				
			}
		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	public void generaJSONCarruselClarosports() {
		try {
			boolean success = false;
			
			//lstNoticias = consultarCarruselClarosportsJSON(parametros.getCarruselCSNumReg());
			lstNoticias = obtenerNoticias("deportes");
			if (lstNoticias != null && lstNoticias.size() > 0) {
				JSONObject respuestaJSON = new JSONObject();
				JSONArray arregloNotas = new JSONArray();
				
				try {
					for (int temp = 0; temp < lstNoticias.size(); temp++) {
						NoticiaRSSDTO nota = (NoticiaRSSDTO) lstNoticias.get(temp);
						
						String rtfContenido = nota.getCL_RTF_CONTENIDO();
						rtfContenido = CodificaCaracteres.deleteBloque(rtfContenido, "<", ">");
						rtfContenido = CodificaCaracteres.deleteBloque(rtfContenido, "[", "]");
						
						JSONObject notaJSON = new JSONObject();
						notaJSON.put("id", CodificaCaracteres.deleteHtmlTag(nota.getFC_ID_CONTENIDO()));
						notaJSON.put("titulo", CodificaCaracteres.deleteHtmlTag(nota.getFC_TITULO()));
						//notaJSON.put("descripcion", RedSocialEmbedding.getEmbedPost(nota.getCL_RTF_CONTENIDO()));
						notaJSON.put("descripcion", CodificaCaracteres.deleteHtmlTag(rtfContenido));
						notaJSON.put("url_imagen", CodificaCaracteres.deleteHtmlTag(parametros.getDominio()+nota.getFC_IMAGEN_PRINCIPAL()));
						notaJSON.put("url_imagen_t1", CodificaCaracteres.deleteHtmlTag(parametros.getDominio()+nota.getFC_IMAGEN_PRINCIPAL().replace("Principal", "imagent1")));
						notaJSON.put("url_imagen_t2", CodificaCaracteres.deleteHtmlTag(parametros.getDominio()+nota.getFC_IMAGEN_PRINCIPAL().replace("Principal", "imagent2")));
						notaJSON.put("url_contenido", CodificaCaracteres.deleteHtmlTag(parametros.getDominio()+"/"+nota.getFcLink()+"?utm_source=carrusel&utm_medium=link&utm_campaign=claroideas"));
						arregloNotas.put(notaJSON);
					}
					SimpleDateFormat iniDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date fecha = new Date();
			    	//Timestamp fechaTimeStamp = new Timestamp(lstNoticias.get(0).getFD_FECHA_MODIFICACION().getTime());
					respuestaJSON.put("mensaje", "exito");
					respuestaJSON.put("codigo", "0");
					respuestaJSON.put("ultima_actualizacion", iniDateFormat.format(fecha));
					respuestaJSON.put("proveedor", "UnoTV");
					respuestaJSON.put("carrusel", arregloNotas);
					success = true;
				} catch (Exception e) {
					log.error("Error en generaJSONCarruselClarosports: ", e);
					success = false;
				}
				if (success) {
					try {
						FileWriter fw = new FileWriter(parametros.getRutaCarpetaJsonMagazine() + parametros.getCarruselCSNombreJson());
						fw.write(respuestaJSON.toString());
						fw.flush();
						fw.close();
					} catch (Exception e) {
						log.error("Error al escribir los archivos: ", e);
					}
					
					Writer wt = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parametros.getRutaCarpetaJsonMagazine() + parametros.getCarruselCSNombreJson()+"_2"), "UTF-8"));
					try {
						wt.write(respuestaJSON.toString());
					}catch(Exception e){
						log.error("Error escribiendo el JSON: ", e);
					}finally {
						wt.close();
					}
					
			        
				}
				
			}
		} catch (Exception e) {
			log.error("Error en generaJSONCarruselClarosports: ", e);
		}

	}
	public void generaTwitterApp() {
		try {
			boolean success = false;
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setUseSSL(true);
			cb.setDebugEnabled(true).setOAuthConsumerKey(
					parametros.getConsumerKey()).setOAuthConsumerSecret(
					parametros.getConsumerSecret()).setOAuthAccessToken(
					parametros.getAccessToken()).setOAuthAccessTokenSecret(
					parametros.getAccessTokenSecret());
			cb.setJSONStoreEnabled(true);
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();
			Paging num = new Paging();
			num.count(100);
			ResponseList<Status> userTimeline = twitter.getUserTimeline(num);
			if (userTimeline.size() > 0) {
				JSONObject respuestaJSON = new JSONObject();
				JSONArray arregloTweets = new JSONArray();
				try {
					for (int i = 0; i < userTimeline.size(); i++) {
						Status st = (Status) userTimeline.get(i);
						JSONObject use = new JSONObject();
						use.put("name", st.getUser().getName());
						use.put("screen_name", st.getUser().getScreenName());
						use.put("profile_image_url", st.getUser().getProfileImageURL());
						JSONObject tw = new JSONObject();
						tw.put("text", st.getText());
						tw.put("created_at", st.getCreatedAt());
						tw.put("user", use);
						arregloTweets.put(tw);
					}
					respuestaJSON.put("mensaje_respuesta", "OK");
					respuestaJSON.put("codigo_respuesta", "0");
					respuestaJSON.put("tweets", arregloTweets);
					success = true;
				} catch (Exception e) {
					log.error("Error: ", e);
					success = false;
				}
				if (success) {
					FileWriter fw = new FileWriter(parametros.getRutaCarpeta() + "twitter_app.json");
					fw.write(respuestaJSON.toString());
					fw.flush();
					fw.close();
				}
//				transfiereWebServer(parametros.getPathShell(), parametros.getRutaCarpeta() + "twitter_app.json", parametros.getRutaDestino());
				
			}
		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	
	/**
	 * @param local
	 * @param remote
	 * @return
	 */
	public boolean transfiereWebServer(String rutaShell, String pathLocal, String pathRemote) {
		boolean success = false;

		String comando = "";
		  
		if(pathLocal.equals("") && pathRemote.equals("")){
			  comando = rutaShell;
		} else{
			  comando = rutaShell + " " + pathLocal+ "* " + pathRemote;
		}
		
		log.info("Comando:  " + comando);
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(comando);
		
			//Para validar la ejecuci�n del shell
	/*		String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(r.exec(comando).getErrorStream()));
			LOG.debug("*****");
			while ((line = input.readLine()) != null) {
				LOG.debug(line);
			}
			input.close();
			LOG.debug("*****"); */
			
			success = true;
		} catch(Exception e) {
			success = false;
			log.error("Ocurrio un error al ejecutar el Shell " + comando + ": ", e);
		}
		return success;
	}
	
	public org.w3c.dom.Document writeNewsML() {
		log.info(".: Ejecutandose...");
		procesoBO = new ProcesoBO();
		try{
			log.info(":::: [INI] generamos archivos por categorias ::::");
			List<CategoriaDTO> lst = procesoBO.getCategorias().getCategotiasLst();
			for(CategoriaDTO dto : lst){
				generaJSON(dto.getFC_ID_CATEGORIA()+".json", dto.getFC_ID_CATEGORIA(), "categoria");
			}
			log.info(":::: [FIN] generamos archivos por categorias ::::");
			log.info(":::: [INI] generamos archivos por seccion ::::");
			List<SeccionDTO> lstSecciones = procesoBO.getSecciones().getSeccionesLst();
			for(SeccionDTO dto : lstSecciones){
				generaJSON(dto.getFC_FRIENDLY_URL()+"sec.json", dto.getFC_ID_SECCION(), "seccion");
			}
			log.info(":::: [FIN] generamos archivos por seccion ::::");
			log.info(":::: [INI] generamos archivos por tipo seccion ::::");
			List<TipoSeccionDTO> lstTipoSecciones = procesoBO.getTipoSecciones().getTipoSeccionesLst();
			for(TipoSeccionDTO dto : lstTipoSecciones){
				generaJSON(dto.getFC_FRIENDLY_URL()+".json", dto.getFC_ID_TIPO_SECCION(), "tipoSeccion");
			}
			log.info(":::: [FIN] generamos archivos por tipo seccion ::::");
			
			
			log.info(":::: [INI] actualizamos monitoreo");
			EscribeArchivoMonitoreo.escribeArchivoMon(parametros);
			log.info(":::: [FIN] actualizamos monitoreo");
		} catch ( Exception e ){
			log.error("[ getInfoJSON ] Ocurrio un error al obtener informacion " + e.getCause());
		}
		
		return null;
	}
	
	/**Metodo que va por las noticias del magazine especificado
	 * @param idMagazine
	 * @return List<NoticiaRSSDTO>
	 * @throws Exception
	 */
	public List<NoticiaRSSDTO> consultarNoticiasMagazineJSON(String idMagazine) throws Exception{
		log.info("idMagazine: "+idMagazine);
		procesoBO = new ProcesoBO();
		List<NoticiaRSSDTO> listaNoticias = procesoBO.consultarNoticiasMagazineJSON(idMagazine).getNoticiasLst();;
		return listaNoticias;
	}
	/**Metodo que va por las noticias del carrusel de clarosports
	 * @param idMagazine
	 * @return List<NoticiaRSSDTO>
	 * @throws Exception
	 */
	public List<NoticiaRSSDTO> consultarCarruselClarosportsJSON(int numRegistros) throws Exception{
		log.info("numRegistros: "+numRegistros);
		procesoBO = new ProcesoBO();
		List<NoticiaRSSDTO> listaNoticias = procesoBO.consultarCarruselClarosportsJSON(numRegistros).getNoticiasLst();;
		return listaNoticias;
	}
	/**
	 * @param idCategoria
	 * @return
	 * @throws Exception
	 */
	public List<NoticiaRSSDTO> obtenerNoticias(String idCategoria) throws Exception{
		log.info("idCategoria: "+idCategoria);
		procesoBO = new ProcesoBO();
		List<NoticiaRSSDTO> listaNoticias = procesoBO.consultarNoticias(idCategoria).getNoticiasLst();;
		return listaNoticias;
	}
	/**
	 * @param idSeccion
	 * @return List<NoticiaRSSDTO>
	 * @throws Exception
	 */
	public List<NoticiaRSSDTO> consultarUltimasPorSeccion(String idSeccion) throws Exception{
		procesoBO = new ProcesoBO();
		List<NoticiaRSSDTO> listaNoticias = procesoBO.consultarUltimasPorSeccion(idSeccion).getNoticiasLst();
		return listaNoticias;
	}
	
	/**
	 * @param idSeccion
	 * @return List<NoticiaRSSDTO>
	 * @throws Exception
	 */
	public List<NoticiaRSSDTO> consultarUltimasPorTipoSeccion(String idSeccion) throws Exception{
		procesoBO = new ProcesoBO();
		List<NoticiaRSSDTO> listaNoticias = procesoBO.consultarUltimasPorTipoSeccion(idSeccion).getNoticiasLst();
		return listaNoticias;
	}
	
	@Autowired
	public void setProcesoBO(ProcesoBO procesoBO) {
		this.procesoBO = procesoBO;
	}

	public ProcesoBO getProcesoBO() {
		return procesoBO;
	}
	
}