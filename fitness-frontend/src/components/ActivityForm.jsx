
import { Box, Button, FormControl, InputLabel, MenuItem, Select, TextField } from '@mui/material'
import React, { act, useState } from 'react'
import { addActivity } from '../services/api';

//This pg shows the activity form that user fills

const ActivityForm = ({ onActivityAdded }) => {

  //backend requires a particular type of object structure and we have used useState to define it
  const [activity, setActivity] = useState({
    type: "RUNNING", duration: '', caloriesBurned: '',
    additionalMetrics: {}
  });

  const handleSubmit = async (e) => {  //called below
    e.preventDefault();
    try {
      await addActivity(activity);   //addActivity is imported as api.js file so basically we call api.js file
      onActivityAdded();
      setActivity({
    type: "RUNNING", duration: '', caloriesBurned: '',
    additionalMetrics: {}
  });
    } catch (error) {
      console.error(error);
    }
  }

  return (
    <Box component="form" sx={{ mb: 2 }} onSubmit={handleSubmit}> {/*onSubmit we call handleSubmit*/}
      <FormControl>
        <InputLabel>Activity Type</InputLabel>
        <Select sx={{ mb: 2 }}
          value={activity.type}
          onChange={(e) => setActivity({...activity, type: e.target.value})}>
            <MenuItem value="RUNNING">Running</MenuItem>
            <MenuItem value="WALKING">Walking</MenuItem>
            <MenuItem value="CYCLING">Cycling</MenuItem>
          </Select>
      </FormControl>
      <TextField fullWidth
        label="Duration (Minutes)"
        type='number'
        sx={{mb: 2}}
        value={activity.duration}
        onChange={(e) => setActivity({...activity, duration: e.target.value})}/>

    <TextField fullWidth
        label="Calories Burned"
        type='number'
        sx={{mb: 2}}
        value={activity.caloriesBurned}
        onChange={(e) => setActivity({...activity, caloriesBurned: e.target.value})}/>

      <Button type='submit' variant='contained'>Add Activity</Button> 
      {/*in above line type='submit' that means then form ke first line se handleSubmit called*/}
    </Box>
  )
}

export default ActivityForm