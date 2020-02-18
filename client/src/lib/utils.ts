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
  setTimeout(() => {
    // eslint-disable-next-line no-restricted-globals
    location.reload();
  }, 500);
};

export const dateCrop = (str: string) => str.replace(/T.+/g, '');
