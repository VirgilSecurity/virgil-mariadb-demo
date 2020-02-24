import React from "react";
import { FormControl, Button, TextField } from "@material-ui/core";
import { useSignUpForm } from "../../lib/utils";
import { PageTitle } from "../../lib/styles";

export interface AddResultProps {
  onSubmit: (res: string) => void;
}

const AddResult: React.FC<AddResultProps> = ({ onSubmit }) => {
  const { inputs, handleInputChange, handleSubmit } = useSignUpForm(() => {
    onSubmit(inputs["text"]);
  });

  return (
    <>
      <PageTitle>Add result</PageTitle>
      <form onSubmit={handleSubmit}>
        <FormControl style={{ width: "100%" }}>
          <TextField
            id="text"
            name="text"
            label="Result"
            required
            multiline
            style={{ margin: "15px 0px" }}
            onChange={handleInputChange}
            rows="4"
            autoFocus={true}
          />
          <Button color="primary" variant="contained" type="submit">
            Submit
          </Button>
        </FormControl>
      </form>
    </>
  );
};

export default AddResult;
