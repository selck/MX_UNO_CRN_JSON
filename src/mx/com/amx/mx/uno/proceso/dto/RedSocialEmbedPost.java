package mx.com.amx.mx.uno.proceso.dto;

import java.io.Serializable;

public class RedSocialEmbedPost implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String red_social;
	
	private String codigo_embebido;
	
	private String cadena_que_sera_reemplazada;

	/**
	 * Obtiene el valor de red_social.
	 * @return valor de red_social.
	 */
	public String getRed_social() {
		return red_social;
	}

	/**
	 * Asigna el valor de red_social.
	 * @param red_social valor de red_social.
	 */
	public void setRed_social(String red_social) {
		this.red_social = red_social;
	}

	/**
	 * Obtiene el valor de codigo_embebido.
	 * @return valor de codigo_embebido.
	 */
	public String getCodigo_embebido() {
		return codigo_embebido;
	}

	/**
	 * Asigna el valor de codigo_embebido.
	 * @param codigo_embebido valor de codigo_embebido.
	 */
	public void setCodigo_embebido(String codigo_embebido) {
		this.codigo_embebido = codigo_embebido;
	}

	/**
	 * Obtiene el valor de cadena_que_sera_reemplazada.
	 * @return valor de cadena_que_sera_reemplazada.
	 */
	public String getCadena_que_sera_reemplazada() {
		return cadena_que_sera_reemplazada;
	}

	/**
	 * Asigna el valor de cadena_que_sera_reemplazada.
	 * @param cadena_que_sera_reemplazada valor de cadena_que_sera_reemplazada.
	 */
	public void setCadena_que_sera_reemplazada(String cadena_que_sera_reemplazada) {
		this.cadena_que_sera_reemplazada = cadena_que_sera_reemplazada;
	}
	
}
