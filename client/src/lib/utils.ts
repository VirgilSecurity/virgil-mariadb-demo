import React,{ useState, FormEvent } from "react";

export const useSignUpForm = (callback: () => void) => {
    const [inputs, setInputs] = useState<any>({});
    const handleSubmit = (event: FormEvent) => {
      if (event) {
        event.preventDefault();
      }
      callback();
    }
    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
      event.persist();
      setInputs((inputs: any) => ({...inputs, [event.target.name]: event.target.value}));
    }
    return {
      handleSubmit,
      handleInputChange,
      inputs
    };
};

export const reloadPage = () => {
  // eslint-disable-next-line no-restricted-globals
  location.reload();
};

export const dateCrop = (str: string) => str.replace(/T.+/g, '');

// tslint:disable:no-any
export function debounce(func: Function, wait: number, immediate?: boolean) {
	let timeout: any;

	return function(this: any) {
		const context = this;
		const args = arguments;
		const later = function() {
			timeout = undefined;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
};
