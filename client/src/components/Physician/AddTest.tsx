import React from "react";
import { FormControl, InputLabel, Input, Button } from "@material-ui/core";
import { useSignUpForm } from "../../lib/utils";
import { ILabTestPost } from "../../lib/Interfaces";
import {
  MuiPickersUtilsProvider,
  KeyboardDatePicker
} from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";
import { H3 } from "../../lib/styles";

export interface AddTestProps {
  onSubmit: (inputs: ILabTestPost) => void;
  patient_id: string;
}

const AddTest: React.FC<AddTestProps> = ({ onSubmit, patient_id }) => {
  const [selectedDate, setSelectedDate] = React.useState<Date>(
    new Date(Date.now())
  );

  const handleDateChange = (date: Date | null) => {
    if (date) {
      setSelectedDate(date);
    }
  };

  const { inputs, handleInputChange, handleSubmit } = useSignUpForm(() => {
    const res: ILabTestPost = {
      id: null,
      name: inputs["text"],
      results: null,
      status: null,
      patient_id: patient_id,
      physician_id: null,
      test_date: selectedDate.toISOString().split("T")[0]
    };
    onSubmit(res);
  });

  return (
    <>
      <H3>New test</H3>
      <form onSubmit={handleSubmit}>
        <FormControl style={{ width: "100%" }}>
          <InputLabel required htmlFor="text">
            Name
          </InputLabel>
          <Input
            required
            name="text"
            autoFocus={true}
            style={{ margin: "15px 0px" }}
            onChange={handleInputChange}
            id="text"
          />
          <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <KeyboardDatePicker
              variant="inline"
              format="yyyy-MM-dd"
              value={selectedDate}
              label="Test date"
              style={{ margin: "15px 0px" }}
              onChange={handleDateChange}
              autoOk={true}
              disablePast={true}
            />
          </MuiPickersUtilsProvider>

          <Button color="primary" variant="contained" type="submit">
            Create
          </Button>
        </FormControl>
      </form>
    </>
  );
};

export default AddTest;
