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

export const useCount = (start: number) => {
  const [count, setCount] = useState(start);

  const getNext = () => {
    setCount(count + 1);
    return count;
  };

  return getNext;
};