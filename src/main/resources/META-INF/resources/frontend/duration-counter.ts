import { LitElement, html, customElement, state} from 'lit-element';
import {
	  PreventAndRedirectCommands,
	  Router,
	  RouterLocation,
	} from '@vaadin/router';
import '@vaadin/vaadin-tabs';
import '@vaadin/vaadin-icon';
import { property } from 'lit/decorators.js';


@customElement('duration-counter')
export class DurationCounter extends LitElement {

	tokenRequiredChar = "$";
	tokenOptionalChar = "?";
	
	@property()
	format? : string;
	
	@property()
	run : boolean = false;

	@property()
	milliseconds: number = 0;
	
	@property()
	increment: number = 1000;
	
	@property()
	up: boolean = true;
	
	running: boolean = false;
	
	formattedString: string = "";
	
	constructor() { 
	    super();
	 }
	
	formatString() {
		var r = this.milliseconds;
		var format = this.format;
		
		var ar = this.formatPart(format,"d",r,86400000);
		format = ar[0] as string;
		r = ar[1] as number;
		
		var ar = this.formatPart(format,"h",r,3600000);
		format = ar[0] as string;
		r = ar[1] as number;
		
		var ar = this.formatPart(format,"m",r,60000);
		format = ar[0] as string;
		r = ar[1] as number;
		
		var ar = this.formatPart(format,"s",r,1000);
		format = ar[0] as string;
		r = ar[1] as number;
		
		var ar = this.formatPart(format,"S",r,1);
		format = ar[0] as string;
		r = ar[1] as number;
		
		this.formattedString = format;
	}
	
	formatPart(format:string, tokenChar: string,r: number,divisor: number) {

		var formatStart = format.indexOf(tokenChar+"{");
		if(formatStart > -1){

			var formatStop = format.indexOf("}",formatStart);
			var formatString = format.substring(formatStart+2,formatStop);
			var optional = formatString.indexOf(this.tokenOptionalChar) > -1;
			var tokenPadChar = optional?this.tokenOptionalChar:this.tokenRequiredChar;
			var token = formatString.substring( formatString.indexOf(tokenPadChar), formatString.lastIndexOf(tokenPadChar)+1);
			

			var value = Math.floor(r / divisor);
			r = r - value*divisor;
			if(value > 0 || !optional) {
				var valueString = value.toString().padStart(token.length,"0");
				var parts = formatString.split(token);
				var output = parts[0]+valueString+(parts.length==2?parts[1]:"");
				format = format.substring(0,formatStart) + output + format.substring(formatStop+1);
			}
			else {
				//remove format block
				format = format.substring(0,formatStart) + format.substring(formatStop+1);
			}
			
		}
		return [format,r];
	}
	
	start() {
		if(!this.running) {
			this.running = true;
			setTimeout(() => {this.runIncrement();}, this.increment);
		}
	}
	
	runIncrement() {
		if(this.run) {
			var old = this.formattedString;
			this.milliseconds = this.up ? this.milliseconds + this.increment : this.milliseconds - this.increment;
			
			this.requestUpdate("formattedString", old);
			setTimeout(()=>{this.runIncrement();}, this.increment);
		}
		else {
			this.running = false;
		}
	}
	

	render() {
		if(this.run && !this.running) {
			this.start();
		}
		this.formatString();
		return html`<span>${this.formattedString}</span>`
  	}


/* 
 * renders this component in the light DOM instead of shadow DOM 
 * not ideal but makes styling easier so we dont have to use slots and/or parts 
 */
 createRenderRoot() {
    return this;
  }
}