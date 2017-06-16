package mx.com.amx.unotv.workflow.controller;

import java.util.Collections;
import java.util.List;

import mx.com.amx.unotv.workflow.dao.ImagesDAO;
import mx.com.amx.unotv.workflow.dao.WorkFlowDAO;
import mx.com.amx.unotv.workflow.dto.ContentDTO;
import mx.com.amx.unotv.workflow.dto.ExtraInfoContentDTO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"workflow-controller"})
public class WorkFlowController {
	
	private static Logger logger=Logger.getLogger(WorkFlowController.class);
	
	private WorkFlowDAO workFlowDAO; 
	private ImagesDAO imagesDAO;
	
	@RequestMapping(value={"getNotasMagazine"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public List<ContentDTO> getNotasMagazine(@RequestParam("idMagazine") String idMagazine, @RequestParam("idContenido") String idContenido){
		try{
			return  (List<ContentDTO>) this.workFlowDAO.getNotasMagazine(idMagazine, idContenido);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [getNotasMagazine]:", e);
			return Collections.emptyList();
		}
	}
	
	@RequestMapping(value={"getIdNotaByName"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public String getIdNotaByName (@RequestBody String name){
		String idContenido="";
		try {
			idContenido=workFlowDAO.getIdNotaByName(name);
		} catch (Exception e) {
			
			logger.error(" Error getIdNotaByName [Controller] ",e );
		}
		return idContenido;
	}
	
	@RequestMapping(value={"insertNotaTag"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean insertNotaTag(@RequestParam("idContenido") String idContenido, @RequestParam("idTag") String idTag){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.insertNotaTag(idContenido, idTag);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [insertNotaTag]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"getRelacionadasbyIdCategoria"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public List<ContentDTO> getRelacionadasbyIdCategoria(@RequestBody ContentDTO content){
		try{
			return  (List<ContentDTO>) this.workFlowDAO.getRelacionadasbyIdCategoria(content.getFcIdCategoria(), content.getFcNombre());
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [getRelacionadasbyIdCategoria]:", e);
			return Collections.emptyList();
		}
	}
	
	
	@RequestMapping(value={"getSequenceImage"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public int getSequenceImage (@RequestBody String image){
		int secuencia=0;
		try {
			secuencia=imagesDAO.getSequenceImage();
		} catch (Exception e) {
			
			logger.error(" Error getSequenceImage [Controller] ",e );
		}
		return secuencia;
	}
	
	@RequestMapping(value={"existeNotaRegistrada"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean existeNotaRegistrada(@RequestBody ContentDTO content){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.existeNotaRegistrada(content.getFcNombre());
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [existeNotaRegistrada]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"insertNotaBD"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean insertNotaBD(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.insertNotaBD(contentDTO);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [insertNotaBD]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"insertNotaHistoricoBD"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean insertNotaHistoricoBD(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.insertNotaHistoricoBD(contentDTO);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [insertNotaHistoricoBD]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"updateNotaBD"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean updateNotaBD(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.updateNotaBD(contentDTO);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [updateNotaBD]:", e);
		}
		return resultado;
	}
	@RequestMapping(value={"updateNotaHistoricoBD"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean updateHistoricoNoticiaBD(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.updateNotaHistoricoBD(contentDTO);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [updateNotaHistoricoBD]:", e);
		}
		return resultado;
	}
	@RequestMapping(value={"deleteNotaBD"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean deleteNoticia(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.deleteNotaBD(contentDTO.getFcIdContenido());
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [deleteNotaBD ]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"deleteNotaHistoricoBD"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean deleteNoticiaHistorico(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.deleteNotaHistoricoBD(contentDTO.getFcIdContenido());
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [deleteNotaHistoricoBD]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"deleteNotaTag"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public Boolean deleteNotaTag(@RequestBody ContentDTO contentDTO){
		Boolean resultado=false;
		try{
			resultado = this.workFlowDAO.deleteNotaTag(contentDTO.getFcIdContenido());
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [deleteNotaTag ]:", e);
		}
		return resultado;
	}
	
	@RequestMapping(value={"getExtraInfoContent"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, headers={"Accept=application/json"})
	@ResponseBody
	public ExtraInfoContentDTO getExtraInfoContent(@RequestBody String friendlyURL){
		ExtraInfoContentDTO extraInfoContentDTO=new ExtraInfoContentDTO();
		try{
			extraInfoContentDTO = this.workFlowDAO.getExtraInfoContent(friendlyURL);
		}
		catch (Exception e){
			logger.error(" Error WorkFlowController [getExtraInfoContent ]:", e);
		}
		return extraInfoContentDTO;
	}
	/**
	 * @return the workFlowDAO
	 */
	public WorkFlowDAO getWorkFlowDAO() {
		return workFlowDAO;
	}

	/**
	 * @param workFlowDAO the workFlowDAO to set
	 */
	@Autowired
	public void setWorkFlowDAO(WorkFlowDAO workFlowDAO) {
		this.workFlowDAO = workFlowDAO;
	}

	/**
	 * @return the imagesDAO
	 */
	public ImagesDAO getImagesDAO() {
		return imagesDAO;
	}

	/**
	 * @param imagesDAO the imagesDAO to set
	 */
	@Autowired
	public void setImagesDAO(ImagesDAO imagesDAO) {
		this.imagesDAO = imagesDAO;
	}
	
	
}
