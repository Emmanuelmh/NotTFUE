package moctezuma;

import java.awt.Color;

import robocode.*;

public class NotTFUE2 extends AdvancedRobot{
	
	private Estado estado;
	private ScannedRobotEvent SRE;
	

	double energiaPrevia = 100;
	  int direccion = 1;
	  int direccionArma = 1;
	  int potencia = 1;
	  
	  public void run() {
		  inicializar();
		  while(true) {
			  setTurnGunRight(99999);
			  switch (estado) {
			  case NADA:
				  doNothing();
				  break;
			  case ACERCARSE:
				  
				  //Se acerca al mismo tiempo que maniobra
				setAhead(SRE.getDistance()/3+20);
		    	setTurnRight(100);
		    	setTurnLeft(100);
		    	estado = Estado.NADA;
				break;
			  case ESQUIVAR:
				  
				//Maniobra en caso de ser impactado
				setBack(150*direccion);
				setTurnLeft(150*-direccion);
				estado = Estado.NADA;
			  	default:
			  		doNothing();
				break;
			}
			  execute();
			  
		  }
	    
	    
	  }
	  
	  private enum Estado{
		  NADA,
		  ACERCARSE,
		  ESQUIVAR
	  }
	  
	  private void inicializar() {
		  setColors(Color.darkGray, Color.cyan, Color.black);
		  estado = Estado.NADA;
	  }
	  public void onScannedRobot(ScannedRobotEvent e) {
		  
		  /*Las instrucciones las colocamos aqui, debido a que para el correcto funcionamiento
		    del algoritmo, es necesario que se ejecute cada vez que escanea un robot y guarde
		    su información constantemente, por lo cual no funcionaba correctamente en una
		    maquina de estados simple*/
		  SRE = e;
		  
		  
		  //El robot se coloca en un angulo tal que cada que se ejecuta, se acerca un poco
		  //más al robot escaneado
		  
	      setTurnRight(e.getBearing()+90-30*direccion);
	    
	      //Medimos el cambio de energía del enemigo para saber si ha disparado, de ser
	      //así, el robot esquiva
	    double cambio = energiaPrevia-e.getEnergy();
	    if (cambio>0 && cambio<=3) {
	         direccion = -direccion;
	         setAhead((e.getDistance()/4+25)*direccion);
	     }
	    
	    direccionArma = -direccionArma;
	    setTurnGunRight(99999*direccionArma);
	    
	    //Medimos la distancia del enemigo para saber si vale la pena que NotTFUE dispare
	    //si está dentro del rango, dispara, si no, se acerca.
	    if(e.getDistance()<350) {
	    	if(getEnergy() > 95) {
	    		fire(3);
	    	}
	    	if(getEnergy() < 50)
	    		fire(2);
	    	else {
	    		fire(potencia);
	    	}
	    }
	    else {
	    	estado = Estado.ACERCARSE;
	    }
	    
	    	energiaPrevia = e.getEnergy();
	  	}
	  
		public void onBulletHit(BulletHitEvent event) {
			
			//Aumenta la potencia si acierta una bala
			if(potencia<3) {
				potencia++;
			}
		}
	  
		public void onBulletMissed(BulletMissedEvent event) {
			
			//Regresa la potencia al mínimo cuando falla 
			potencia = 1;
		}
		
		public void onHitRobot(HitRobotEvent event) {
			
			estado = Estado.ESQUIVAR;
		}
		@Override
		public void onHitByBullet(HitByBulletEvent event) {
			estado = Estado.ESQUIVAR;
		}
}